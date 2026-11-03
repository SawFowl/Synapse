package sawfowl.synapse.implementapi.config.locale;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.stream.Stream;


import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.Locales.EnumLocales;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.config.locale.PluginLocale;
import sawfowl.synapse.api.config.locale.ReferencedLocale;
import sawfowl.synapse.api.config.locale.Translation;
import sawfowl.synapse.api.services.LocaleService;
import sawfowl.synapse.configure.Config;
import sawfowl.synapse.implementapi.config.FormatMigration;
import sawfowl.synapse.utils.WatchRunner;

public class ILocalesList<T extends Translation> implements LocalesList<T> {

	public static ILocalesList<? extends Translation> create(PluginContainer container, Path localesDir, LocaleService localeService) {
		return new ILocalesList<>(container, localesDir, localeService);
	}

	private static Config getConfig() {
		return SynapsePlugin.getConfig();
	}

	// Read on every localized message and changed by the file watcher thread.
	private final Map<Locale, PluginLocale> locales = new ConcurrentHashMap<>();
	private Path path;
	private PluginContainer container;
	private Class<T> reference;
	private static final String DOT = ".";
	private final LocaleService localeService;
	private ILocalesList(PluginContainer container, Path configDirectory, LocaleService localeService) {
		this.container = container;
		this.localeService = localeService;
		if(getConfig() != null) {
			String dir = getConfig().getLocalesSettings(container).getPath()
					.replace("{SYNAPSE_PATH}", SynapsePlugin.getSynapsePluginConfigDir())
					.replace("{PATH_SEPARATOR}", File.separator)
					.replace("{PLUGIN_CONFIG_PATH}", SynapsePlugin.getMainConfigDir() + File.separator + container.getDescription().getId() + File.separator + "locales");
				if(dir.endsWith(File.separator)) dir += "locales";
				if(dir.endsWith(SynapsePlugin.getSynapsePluginConfigDir())) dir += File.separator + "locales" + File.separator + container.getDescription().getId();
			path = Path.of(dir);
			dir = null;
		} else path = configDirectory.resolve("locales");
		createFolders(path, path.toFile());
		if(path.toFile().exists() && path.toFile().isDirectory()) for(File file : path.toFile().listFiles()) {
			if(file.getName().startsWith(DOT) || !file.getName().contains(DOT) || file.getName().endsWith(DOT)) continue;
			String[] nameAndExtension = split(file.getName(), '.');
			if(EnumLocales.isValisTag(nameAndExtension[0]) && ConfigTypes.isValidExtension(nameAndExtension[1])) {
				if(localeService.getDefaultReference(container) == null) {
					createSimpleTranslation(ConfigTypes.getTypeByExtension(nameAndExtension[1]), EnumLocales.find(nameAndExtension[0]));
				} else createReferencedTranslation(ConfigTypes.getTypeByExtension(nameAndExtension[1]), EnumLocales.find(nameAndExtension[0]), localeService.getDefaultReference(container));
			}
			nameAndExtension = null;
		}
		saveAssetLocales();
	}

	@Override
	public PluginLocale createSimpleTranslation(ConfigTypes configType, Locale locale) {
		// pause/resume always in pairs: the early returns used to skip the second call and leave the watcher toggled.
		WatchRunner.pause();
		try {
			return createSimpleTranslationUnpaused(configType, locale);
		} finally {
			WatchRunner.resume();
		}
	}

	private PluginLocale createSimpleTranslationUnpaused(ConfigTypes configType, Locale locale) {
		Objects.requireNonNull(locale);
		if(configType == null) configType = getConfig() == null ? ConfigTypes.HOCON : getConfig().getLocalesSettings(container).getType();
		PluginLocale created = isForced()
			? FormatMigration.open(path, locale.toLanguageTag(), getConfig().getLocalesSettings(container).getType(), type -> IPluginLocale.create(path, type, locale, this))
			: IPluginLocale.create(path, configType, locale, this);
		locales.put(locale, created);
		return created;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O extends T> ReferencedLocale<O> createReferencedTranslation(ConfigTypes configType, Locale locale, Class<O> clazz) {
		WatchRunner.pause();
		try {
			return createReferencedTranslationUnpaused(configType, locale, clazz);
		} finally {
			WatchRunner.resume();
		}
	}

	@SuppressWarnings("unchecked")
	private <O extends T> ReferencedLocale<O> createReferencedTranslationUnpaused(ConfigTypes configType, Locale locale, Class<O> clazz) {
		Objects.requireNonNull(locale);
		Objects.requireNonNull(clazz);
		if(reference == null) reference = (Class<T>) clazz;
		if(configType == null) configType = getConfig() == null ? ConfigTypes.HOCON : getConfig().getLocalesSettings(container).getType();
		ReferencedLocale<O> created = isForced()
			? FormatMigration.open(path, locale.toLanguageTag(), getConfig().getLocalesSettings(container).getType(), type -> IReferencedLocale.create(path, type, clazz, locale))
			: IReferencedLocale.create(path, configType, clazz, locale);
		locales.put(locale, created);
		return created;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O extends T> ReferencedLocale<O> createReferencedTranslation(ConfigTypes configType, Locale locale, O object) {
		WatchRunner.pause();
		try {
			return createReferencedTranslationUnpaused(configType, locale, object);
		} finally {
			WatchRunner.resume();
		}
	}

	@SuppressWarnings("unchecked")
	private <O extends T> ReferencedLocale<O> createReferencedTranslationUnpaused(ConfigTypes configType, Locale locale, O object) {
		Objects.requireNonNull(locale);
		Objects.requireNonNull(object);
		if(reference == null) reference = (Class<T>) object.getClass();
		if(configType == null) configType = getConfig() == null ? ConfigTypes.HOCON : getConfig().getLocalesSettings(container).getType();
		ReferencedLocale<O> created = isForced()
			? FormatMigration.open(path, locale.toLanguageTag(), getConfig().getLocalesSettings(container).getType(), type -> IReferencedLocale.create(path, type, object, locale))
			: IReferencedLocale.create(path, configType, object, locale);
		locales.put(locale, created);
		return created;
	}

	/**
	 * Locales are moved to the format from the Synapse config.
	 */
	private boolean isForced() {
		return getConfig() != null && getConfig().getLocalesSettings(container).isForcedUse();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L extends PluginLocale> L getSimple(Locale locale) throws ClassCastException {
		// The locale may be null (a player whose client has not sent its settings yet).
		PluginLocale found = locale == null ? null : locales.get(locale);
		return (L) (found != null ? found : locales.get(Locales.DEFAULT));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L extends PluginLocale> L remove(Locale locale) throws ClassCastException {
		return (L) (locale == null ? null : locales.remove(locale));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L extends PluginLocale> Stream<L> stream() {
		return (Stream<L>) locales.values().stream();
	}

	@Override
	public void forEach(Consumer<? super PluginLocale> action) {
		locales.values().forEach(action);
	}

	@Override
	public boolean contains(Locale locale) {
		return locale != null && locales.containsKey(locale);
	}

	@Override
	public int size() {
		return locales.size();
	}
	@Override
	public boolean isEmpy() {
		return locales.isEmpty();
	}

	@Override
	public String toString() {
		return "LocalesList[plugin=" + container.getDescription().getId() + ", path=" + path.toFile().getAbsolutePath() + ", locales=" + locales.keySet().stream().map(Locale::toLanguageTag).toList() + "]";
	}

	public void saveAssetLocales() {
		createFolders(path, path.toFile());
		container.getDescription().getSource().ifPresent(this::saveAssets);
		updateWatch();
	}

	public Path getPath() {
		return path;
	}

	/**
	 * Copies bundled locales {@code assets/<plugin id>/lang/<tag>.<extension>} out of the plugin jar.<br>
	 * One pass over the jar. Before, the jar was opened and scanned for every locale and every
	 * format (75 x 8 = 600 times per plugin), and the entry names were compared with a leading
	 * "/" that jar entries never have, so nothing was ever extracted.
	 */
	private void saveAssets(Path pluginFile) {
		String prefix = "assets/" + getPluginID() + "/lang/";
		try(var jarFile = new JarFile(pluginFile.toFile())) {
			var entries = jarFile.entries();
			while(entries.hasMoreElements()) {
				var entry = entries.nextElement();
				if(entry.isDirectory() || !entry.getName().startsWith(prefix)) continue;
				String fileName = entry.getName().substring(prefix.length());
				int dot = fileName.lastIndexOf('.');
				if(fileName.contains("/") || dot <= 0) continue;
				String tag = fileName.substring(0, dot);
				ConfigTypes configType = ConfigTypes.getTypeByExtension(fileName.substring(dot + 1));
				if(configType == ConfigTypes.UNKNOWN || !EnumLocales.isValisTag(tag)) continue;
				Locale locale = EnumLocales.find(tag);
				File localeFile = path.resolve(locale.toLanguageTag() + configType.toString()).toFile();
				if(localeFile.exists() || contains(locale)) continue;
				try(var inputStream = jarFile.getInputStream(entry)) {
					Files.copy(inputStream, localeFile.toPath());
				}
				// Same rule as for the files already in the folder: referenced if the plugin has a reference class.
				Class<T> referenceClass = reference != null ? reference : localeService.getDefaultReference(container);
				if(referenceClass == null) {
					createSimpleTranslation(configType, locale);
				} else createReferencedTranslation(configType, locale, referenceClass);
			}
		} catch (IOException e) {
			SynapsePlugin.getLogger().error("Could not extract the locales of " + getPluginID(), e);
		}
	}

	private void updateWatch() {
		WatchRunner.initPlugin(container, path);
	}

	private String getPluginID() {
		return container.getDescription().getId();
	}

	private void createFolders(Path path, File file) {
		// The old recursion created only the last directory, so a locales folder
		// inside a not yet existing plugin folder was never created.
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			SynapsePlugin.getLogger().error("Could not create the locales folder " + path, e);
		}
	}

	private String[] split(String string, char ch) {
		int off = 0;
		int next;
		ArrayList<String> list = new ArrayList<>();
		while ((next = string.indexOf(ch, off)) != -1) {
			list.add(string.substring(off, next));
			off = next + 1;
		}
		// If no match was found, return this
		if (off == 0) return new String[] {string};

		// Add remaining segment
		list.add(string.substring(off, string.length()));

		// Construct result
		int resultSize = list.size();
		while (resultSize > 0 && list.get(resultSize - 1).isEmpty()) {
			resultSize--;
		}
		return list.subList(0, resultSize).toArray(new String[resultSize]);
	}

}
