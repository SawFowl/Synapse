package sawfowl.synapse.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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

class Watcher {

	private WatchService watchService;
	private boolean freeze = true;
	private Set<String> registered = new HashSet<>();
	private Map<Path, PluginContainer> paths = new HashMap<>();
	private Set<UpdateInfo> updateInfo = new HashSet<>();
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
		freeze = !freeze;
	}

	void enable() {
		freeze = false;
	}

	void register(PluginContainer container, Path localesDir) {
		if(!registered.contains(container.getDescription().getId())) try {
			if(!localesDir.toFile().exists()) localesDir.toFile().mkdir();
			localesDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
			registered.add(container.getDescription().getId());
			paths.put(localesDir, container);
		} catch (IOException e) {
			getLogger().error(e.getLocalizedMessage());
		}
	}

	void startWatch() {
		
		if(freeze) {
			try {
				watchService.take().reset();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		if(!updateInfo.isEmpty()) updateInfo.removeIf(info -> System.currentTimeMillis() - info.time > 1000);
		try {
			while(isResetKey(watchService.take()));
		} catch (InterruptedException e) {
			getLogger().error(e.getLocalizedMessage());
		}
	}

	private boolean isResetKey(WatchKey key) {
		if(freeze) return false;
		for(WatchEvent<?> event : key.pollEvents()) {
			if(paths.containsKey((Path) key.watchable())) {
				work(event, paths.get((Path) key.watchable()), (Path) key.watchable(), event.context().toString());
			} else if(((Path) key.watchable()).getParent() != null) {
				work(event, (Path) key.watchable(), (((Path) key.watchable()).getParent()).toFile().getName(), event.context().toString());
			}
		}
		return key.reset();
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
			localeService.getLocales(container).remove(locale);
			logger.info("[FileWatcher] " + getMessages().getRemove(locale, container));
			fireEvent(new LocaleEvent.Delete() {

				@Override
				public String getPluginId() {
					return container.getDescription().getId();
				}

				@Override
				public Locale getLocale() {
					return locale;
				}

				@Override
				public String getFileName() {
					return fileName;
				}
				
			});
		}).delay(200, TimeUnit.MILLISECONDS).schedule();
	}

	private void onCreate(PluginContainer container, Locale locale, ConfigTypes type) {
		if(!localeService.getLocales(container).contains(locale)) create(container, locale, type, System.currentTimeMillis());
	}

	private void create(PluginContainer container, Locale locale, ConfigTypes type, long time) {
		logger.info("[FileWatcher] " + getMessages().getAdd(locale, type, container));
		this.updateInfo.add(new UpdateInfo(time, locale, container));
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
		UpdateInfo updateInfo = this.updateInfo.stream().filter(info -> info.locale.equals(locale) && info.container.getDescription().getId().equals(container.getDescription().getId())).findFirst().orElse(null);
		if(updateInfo == null) {
			if(!localeService.getLocales(container).contains(locale) || localeService.getLocales(container).getSimple(locale).getType() != type) return;
			PluginLocale pluginLocale = localeService.getLocales(container).getSimple(locale);
			pluginLocale.load();
			this.updateInfo.add(new UpdateInfo(System.currentTimeMillis(), locale, container));
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
		} else this.updateInfo.remove(updateInfo);
	}

	private boolean isValidFile(String fileName, String extension) {
		return existTag(fileName) && ConfigTypes.isValidExtension(extension);
	}

	void stopWatch() {
		freeze = true;
	}

	private Logger getLogger() {
		return logger;
	}

	private boolean existTag(String locale) {
		return Stream.of(EnumLocales.values()).filter(value -> value.getTag().equals(locale)).findFirst().isPresent();
	}

	private LoggerMessages getMessages() {
		return SynapsePlugin.getLocales().getSystemAsReferenced().getLoggerMessages();
	}

	private void fireEvent(LocaleEvent event) {
		Synapse.getProxy().getEventManager().fireAndForget(event);
	}

	private class UpdateInfo {
		final PluginContainer container;
		final Locale locale;
		final long time;
		public UpdateInfo(long time, Locale locale, PluginContainer container) {
			this.container = container;
			this.locale = locale;
			this.time = time;
		}

		@Override
		public int hashCode() {
			return Objects.hash(container, locale, time);
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null || getClass() != obj.getClass()) return false;
			return Objects.equals(container.getDescription().getId(), ((UpdateInfo) obj).container.getDescription().getId()) && Objects.equals(locale, ((UpdateInfo) obj).locale) && time == ((UpdateInfo) obj).time;
		}

	}

}
