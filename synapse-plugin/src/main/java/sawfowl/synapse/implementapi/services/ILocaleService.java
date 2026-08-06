package sawfowl.synapse.implementapi.services;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.Locales.EnumLocales;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.config.locale.Translation;
import sawfowl.synapse.api.services.LocaleService;
import sawfowl.synapse.implementapi.config.locale.ILocalesList;
import sawfowl.synapse.utils.WatchRunner;

public class ILocaleService implements LocaleService {

	private static ILocaleService instance;

	public static void createInstance(Path path) {
		if(instance == null) instance = new ILocaleService(path);
	}

	public static ILocaleService getInstance() {
		return instance;
	}

	private final Locale systemOrDefault;
	private final Map<String, Class<? extends Translation>> defaultReferences = new HashMap<>();
	private Map<String, LocalesList<? extends Translation>> pluginLocales = new HashMap<>();
	private final Path configDirectory;
	private ILocaleService(Path path) {
		configDirectory = path;
		systemOrDefault = EnumLocales.isValisTag(Locale.getDefault().toLanguageTag()) ? Locale.getDefault() : Locales.DEFAULT;
		WatchRunner.createInstance(this, SynapsePlugin.getLogger());
	}

	@Override
	public Locale getSystemOrDefaultLocale() {
		return systemOrDefault;
	}

	@Override
	public <T extends Translation> void setDefaultReference(PluginContainer container, Class<T> defaultReference) {
		if(defaultReferences.containsKey(container.getDescription().getId())) defaultReferences.remove(container.getDescription().getId());
		if(defaultReference != null) defaultReferences.put(container.getDescription().getId(), defaultReference);
	}

	@Override
	public <T extends Translation> Class<T> getDefaultReference(PluginContainer container) {
		return getDefaultReference(container.getDescription().getId());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Translation> Class<T> getDefaultReference(String pluginID) {
		return defaultReferences.containsKey(pluginID) ? (Class<T>) defaultReferences.get(pluginID) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Translation> LocalesList<T> createLocales(PluginContainer container) {
		if(pluginLocales.containsKey(container.getDescription().getId())) return getLocales(container);
		return (LocalesList<T>) addLocales(container, ILocalesList.create(container, configDirectory, this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Translation> LocalesList<T> createLocales(PluginContainer container, Class<? extends T> translationReference) {
		if(pluginLocales.containsKey(container.getDescription().getId())) return getLocales(container);
		if(!defaultReferences.containsKey(container.getDescription().getId())) defaultReferences.put(container.getDescription().getId(), translationReference);
		return (LocalesList<T>) addLocales(container, ILocalesList.create(container, configDirectory, this));
	}

	@Override
	public <T extends Translation> LocalesList<T> getLocales(PluginContainer container) {
		return getLocales(container.getDescription().getId());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Translation> LocalesList<T> getLocales(String plugin) {
		return (LocalesList<T>) pluginLocales.get(plugin);
	}

	@Override
	public boolean localesExist(PluginContainer container) {
		return localesExist(container.getDescription().getId());
	}

	@Override
	public boolean localesExist(String plugin) {
		return pluginLocales.containsKey(plugin);
	}

	private <T extends Translation> LocalesList<T> addLocales(PluginContainer container, ILocalesList<T> list) {
		pluginLocales.put(container.getDescription().getId(), list);
		WatchRunner.initPlugin(container, list.getPath());
		return list;
	}

}
