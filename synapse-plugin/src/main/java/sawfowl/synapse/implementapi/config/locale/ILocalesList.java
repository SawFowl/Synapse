package sawfowl.synapse.implementapi.config.locale;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.spongepowered.configurate.ConfigurateException;

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
import sawfowl.synapse.utils.WatchRunner;

public class ILocalesList<T extends Translation> implements LocalesList<T> {

	public static ILocalesList<? extends Translation> create(PluginContainer container, Path localesDir, LocaleService localeService) {
		return new ILocalesList<>(container, localesDir, localeService);
	}

	private static Config getConfig() {
		return SynapsePlugin.getConfig();
	}

	private Map<Locale, PluginLocale> locales = new HashMap<>();
	private Path path;
	private PluginContainer container;
	private Class<T> reference;
	private static final String DOT = ".";
	private ILocalesList(PluginContainer container, Path configDirectory, LocaleService localeService) {
		this.container = container;
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
		Objects.requireNonNull(locale);
		WatchRunner.pause();
		if(configType == null) configType = getConfig() == null ? ConfigTypes.HOCON : getConfig().getLocalesSettings(container).getType();
		if(getConfig() != null && getConfig().getLocalesSettings(container).isForcedUse() && !configType.comparableType(getConfig().getLocalesSettings(container).getType())) {
			if(path.resolve(locale.toLanguageTag() + getConfig().getLocalesSettings(container).getType().toString()).toFile().exists()) {
				locales.put(locale, IPluginLocale.create(path, getConfig().getLocalesSettings(container).getType(), locale, this));
				return locales.get(locale); 
			}
			PluginLocale updated = IPluginLocale.create(path, getConfig().getLocalesSettings(container).getType(), locale, this);
			PluginLocale old = null;
			for(File file : path.toFile().listFiles()) {
				if(!file.getName().contains(locale.toLanguageTag())) continue;
				ConfigTypes type = ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(file.getName()));
				if(type  == ConfigTypes.UNKNOWN || type.comparableType(getConfig().getLocalesSettings(container).getType())) continue;
				if(old == null) {
					old = IPluginLocale.create(path, configType, locale, this);
					try {
						updated.getLoader().save(old.getRootNode());
						old.getPath().toFile().delete();
						old = null;
					} catch (ConfigurateException e) {
						e.printStackTrace();
						updated.getPath().toFile().delete();
					}
				} else file.delete();
			}
			if(locales.containsKey(locale)) locales.remove(locale);
			updated.load();
			locales.put(locale, updated);
		} else locales.put(locale, IPluginLocale.create(path, configType, locale, this));
		WatchRunner.pause();
		return locales.get(locale);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O extends T> ReferencedLocale<O> createReferencedTranslation(ConfigTypes configType, Locale locale, Class<O> clazz) {
		Objects.requireNonNull(locale);
		Objects.requireNonNull(clazz);
		WatchRunner.pause();
		if(reference == null) reference = (Class<T>) clazz;
		if(configType == null) configType = getConfig() == null ? ConfigTypes.HOCON : getConfig().getLocalesSettings(container).getType();
		if(getConfig() != null && getConfig().getLocalesSettings(container).isForcedUse()) {
			if(path.resolve(locale.toLanguageTag() + getConfig().getLocalesSettings(container).getType().toString()).toFile().exists()) {
				locales.put(locale, IReferencedLocale.create(path, getConfig().getLocalesSettings(container).getType(), clazz, locale));
				return (ReferencedLocale<O>) locales.get(locale); 
			}
			IReferencedLocale<O> updated = IReferencedLocale.create(path, getConfig().getLocalesSettings(container).getType(), clazz, locale);
			ReferencedLocale<O> old = null;
			for(File file : path.toFile().listFiles()) {
				if(!file.getName().contains(locale.toLanguageTag())) continue;
				ConfigTypes type = ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(file.getName()));
				if(type  == ConfigTypes.UNKNOWN || type.comparableType(getConfig().getLocalesSettings(container).getType())) continue;
				if(old == null) {
					//old = ConfigImpl.create(path, locale.toLanguageTag(), configType, updated.getSerializers());
					old = IReferencedLocale.create(path, configType, clazz, locale);
					old.load();
					//updated.save(old.get());
					try {
						updated.getLoader().save(old.getRootNode());
						old.getPath().toFile().delete();
						old = null;
					} catch (ConfigurateException e) {
						e.printStackTrace();
						updated.getPath().toFile().delete();
					}
				} else file.delete();
			}
			if(locales.containsKey(locale)) locales.remove(locale);
			updated.load();
			locales.put(locale, updated);
		} else locales.put(locale, IReferencedLocale.create(path, configType, clazz, locale));
		if(reference == null) reference = (Class<T>) clazz;
		WatchRunner.pause();
		return (ReferencedLocale<O>) locales.get(locale);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O extends T> ReferencedLocale<O> createReferencedTranslation(ConfigTypes configType, Locale locale, O object) {
		Objects.requireNonNull(locale);
		Objects.requireNonNull(object);
		WatchRunner.pause();
		if(reference == null) reference = (Class<T>) object.getClass();
		if(configType == null) configType = getConfig() == null ? ConfigTypes.HOCON : getConfig().getLocalesSettings(container).getType();
		if(getConfig() != null && getConfig().getLocalesSettings(container).isForcedUse()) {
			if(path.resolve(locale.toLanguageTag() + getConfig().getLocalesSettings(container).getType().toString()).toFile().exists()) {
				locales.put(locale, IReferencedLocale.create(path, getConfig().getLocalesSettings(container).getType(), object, locale));
				return (ReferencedLocale<O>) locales.get(locale); 
			}
			IReferencedLocale<O> updated = IReferencedLocale.create(path, getConfig().getLocalesSettings(container).getType(), object, locale);
			ReferencedLocale<O> old = null;
			for(File file : path.toFile().listFiles()) {
				if(!file.getName().contains(locale.toLanguageTag())) continue;
				ConfigTypes type = ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(file.getName()));
				if(type  == ConfigTypes.UNKNOWN || type.comparableType(getConfig().getLocalesSettings(container).getType())) continue;
				if(old == null) {
					old = IReferencedLocale.create(path, configType, object, locale);
					old.load();
					updated.save(old.get());
					try {
						updated.getLoader().save(old.getRootNode());
						old.getPath().toFile().delete();
						old = null;
					} catch (ConfigurateException e) {
						e.printStackTrace();
						updated.getPath().toFile().delete();
					}
				} else file.delete();
			}
			if(locales.containsKey(locale)) locales.remove(locale);
			updated.load();
			locales.put(locale, updated);
		} else locales.put(locale, IReferencedLocale.create(path, configType, object, locale));
		if(reference == null) reference = (Class<T>) object.getClass();
		WatchRunner.pause();
		return (ReferencedLocale<O>) locales.get(locale);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L extends PluginLocale> L getSimple(Locale locale) throws ClassCastException {
		return (L) (locales.containsKey(locale) ? locales.get(locale) : locales.get(Locales.DEFAULT));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L extends PluginLocale> L remove(Locale locale) throws ClassCastException {
		return (L) locales.remove(locale);
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
		return locales.containsKey(locale);
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
		File localePath = this.path.toFile();
		if(!localePath.exists()) localePath.mkdir();
		for(Locale locale : EnumLocales.getLocales()) saveAssets(locale);
		updateWatch();
	}

	public Path getPath() {
		return path;
	}

	private void saveAssets(Locale locale) {
		for(ConfigTypes configType : ConfigTypes.values()) {
			var configTypeName = configType.toString();
			container.getDescription().getSource().ifPresent(pluginFile -> {
				try(var jarFile = new JarFile(pluginFile.toFile())) {
					var entries = jarFile.entries();
					while(entries.hasMoreElements()) {
						var entry = entries.nextElement();
						if(!entry.isDirectory() && entry.getName().startsWith("/assets/" + getPluginID() + "/lang/" + locale.toLanguageTag() + configTypeName)) {
							var inputStream = jarFile.getInputStream(entry);
							File localeFile = path.resolve(locale.toLanguageTag() + configTypeName).toFile();
							if(!localeFile.exists() && !contains(locale)) {
								try {
									Files.copy(inputStream, localeFile.toPath());
									//container.logger().info(SynapsePlugin.getLocales().getSystemAsReferenced().getLoggerMessages().getSaveAsset(locale, configType, getPluginID()));
									if(reference == null) {
										createSimpleTranslation(configType, locale);
									} else createReferencedTranslation(configType, locale, reference);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void updateWatch() {
		WatchRunner.initPlugin(container, path);
	}

	private String getPluginID() {
		return container.getDescription().getId();
	}

	private void createFolders(Path path, File file) {
		if(!file.exists() && path.getParent() != null) {
			createFolders(path.getParent(), path.toFile());
			file.mkdir();
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
