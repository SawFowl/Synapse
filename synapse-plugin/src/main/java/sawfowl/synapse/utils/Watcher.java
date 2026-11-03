package sawfowl.synapse.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.Locales.EnumLocales;
import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.locale.PluginLocale;
import sawfowl.synapse.api.event.LocaleEvent;
import sawfowl.synapse.api.services.LocaleService;
import sawfowl.synapse.configure.localization.LoggerMessages;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

class Watcher {

	/** Time window in which a repeated MODIFY of the same locale counts as a duplicate. */
	private static final long DUPLICATE_WINDOW_MS = 1000;
	private WatchService watchService;
	private volatile boolean enabled = false;
	private final AtomicInteger paused = new AtomicInteger();
	private final Set<String> registered = ConcurrentHashMap.newKeySet();
	private final Map<Path, PluginContainer> paths = new ConcurrentHashMap<>();
	/** plugin id + locale -> time of the last handled create/reload. */
	private final Map<String, Long> lastHandled = new ConcurrentHashMap<>();
	private LocaleService localeService;
	private Logger logger;
	Watcher(LocaleService localeService, Logger logger) {
		this.localeService = localeService;
		this.logger = logger;
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	void pause() {
		paused.incrementAndGet();
	}

	void resume() {
		paused.updateAndGet(value -> Math.max(0, value - 1));
	}

	void enable() {
		enabled = true;
	}

	void register(PluginContainer container, Path localesDir) {
		if(!registered.contains(container.getDescription().getId())) try {
			Files.createDirectories(localesDir);
			localesDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
			registered.add(container.getDescription().getId());
			paths.put(localesDir, container);
		} catch (IOException e) {
			getLogger().error(e.getLocalizedMessage());
		}
	}

	/**
	 * Waits for one directory's events and handles them.<br>
	 * The key is always drained and reset: before, a paused watcher either returned without
	 * reset (that directory never reported anything again) or reset without draining (the key
	 * came straight back and the thread spun at full CPU).
	 */
	void startWatch() throws InterruptedException {
		WatchKey key = watchService.take();
		try {
			Path dir = (Path) key.watchable();
			for(WatchEvent<?> event : key.pollEvents()) {
				// OVERFLOW has no file name; its null context used to kill the watcher thread.
				if(event.kind() == OVERFLOW || event.context() == null) continue;
				if(!enabled || paused.get() > 0) continue;
				PluginContainer container = paths.get(dir);
				if(container != null) {
					work(event, container, dir, event.context().toString());
				} else if(dir.getParent() != null) {
					work(event, dir, dir.getParent().toFile().getName(), event.context().toString());
				}
			}
		} finally {
			key.reset();
		}
	}

	private void work(WatchEvent<?> event, Path path, String plugin, String file) {
		work(event, Synapse.getProxy().getPluginManager().getPlugin(plugin).orElse(null), path, file);
	}

	private void work(WatchEvent<?> event, PluginContainer container, Path path, String file) {
		if(container != null && !file.contains(".tmp") && file.contains(".")) work(event, container, path, file.split("\\."), file);
	}

	private void work(WatchEvent<?> event, PluginContainer container, Path path, String[] file, String fileName) {
		if(file.length == 2 && isValidFile(file[0], file[1])) work(event, container, path, EnumLocales.find(file[0]), ConfigTypes.getTypeByExtension(file[1]), fileName);
	}

	private void work(WatchEvent<?> event, PluginContainer container, Path path, Locale locale, ConfigTypes type, String fileName) {
		if(event.kind() == ENTRY_CREATE) {
			onCreate(container, locale, type);
		} else if(event.kind() == ENTRY_MODIFY) {
			onModify(container, locale, type);
		} if(event.kind() == ENTRY_DELETE && locale != Locales.DEFAULT) Synapse.getProxy().getScheduler().buildTask(SynapsePlugin.getInstance(), () -> {
			if(path.resolve(fileName).toFile().exists()) return;
			// Only if the locale was loaded from this very file. Converting a locale to another
			// format deletes the old file, and that used to remove the freshly converted locale.
			var locales = localeService.getLocales(container);
			if(locales == null || !locales.contains(locale) || !fileName.equals(locales.getSimple(locale).getPath().getFileName().toString())) return;
			localeService.getLocales(container).remove(locale);
			logger.info("[FileWatcher] " + getMessages().getRemove(locale, container));
			fireEvent(new DeleteEvent(container.getDescription().getId(), locale, fileName));
		}).delay(200, TimeUnit.MILLISECONDS).schedule();
	}

	private void onCreate(PluginContainer container, Locale locale, ConfigTypes type) {
		if(!localeService.getLocales(container).contains(locale)) create(container, locale, type, System.currentTimeMillis());
	}

	private void create(PluginContainer container, Locale locale, ConfigTypes type, long time) {
		logger.info("[FileWatcher] " + getMessages().getAdd(locale, type, container));
		lastHandled.put(key(container, locale), time);
		PluginLocale pluginLocale = localeService.getDefaultReference(container) == null
			?
			localeService.getLocales(container).createSimpleTranslation(type, locale)
			:
			localeService.getLocales(container).createReferencedTranslation(type, locale, localeService.getDefaultReference(container));
		fireEvent(new LocaleEvent.Create() {

			@Override
			public String getPluginId() {
				return container.getDescription().getId();
			}

			@Override
			public PluginLocale getLocaleConfig() {
				return pluginLocale;
			}

			@Override
			public Locale getLocale() {
				return locale;
			}

			@Override
			public String configType() {
				return type.getExtension();
			}

		});
	}

	private void onModify(PluginContainer container, Locale locale, ConfigTypes type) {
		// Editors often report one save as several MODIFY events: the ones within a second
		// of the last handled event are duplicates. This used to be counted by events instead of
		// time (the clean-up never ran), so a single-event save could make the next save be ignored.
		long now = System.currentTimeMillis();
		Long last = lastHandled.get(key(container, locale));
		if(last != null && now - last < DUPLICATE_WINDOW_MS) return;
		if(!localeService.getLocales(container).contains(locale) || localeService.getLocales(container).getSimple(locale).getType() != type) return;
		PluginLocale pluginLocale = localeService.getLocales(container).getSimple(locale);
		pluginLocale.load();
		lastHandled.put(key(container, locale), now);
		logger.info("[FileWatcher] " + getMessages().getReload(locale, type, container));
		fireEvent(new LocaleEvent.Reload() {

			@Override
			public String getPluginId() {
				return container.getDescription().getId();
			}

			@Override
			public PluginLocale getLocaleConfig() {
				return pluginLocale;
			}

			@Override
			public Locale getLocale() {
				return locale;
			}

		});
	}

	private static String key(PluginContainer container, Locale locale) {
		return container.getDescription().getId() + "/" + locale.toLanguageTag();
	}

	private boolean isValidFile(String fileName, String extension) {
		return existTag(fileName) && ConfigTypes.isValidExtension(extension);
	}

	void stopWatch() {
		enabled = false;
		try {
			watchService.close();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	private Logger getLogger() {
		return logger;
	}

	private boolean existTag(String locale) {
		return EnumLocales.isValisTag(locale);
	}

	private LoggerMessages getMessages() {
		return SynapsePlugin.getLocales().getSystemAsReferenced().getLoggerMessages();
	}

	private void fireEvent(LocaleEvent event) {
		Synapse.getProxy().getEventManager().fireAndForget(event);
	}

	private record DeleteEvent(String pluginId, Locale locale, String file) implements LocaleEvent.Delete {

		@Override
		public String getPluginId() {
			return pluginId;
		}

		@Override
		public Locale getLocale() {
			return locale;
		}

		@Override
		public String getFileName() {
			return file;
		}
		
	}

}
