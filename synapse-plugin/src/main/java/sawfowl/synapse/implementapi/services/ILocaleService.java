package sawfowl.synapse.implementapi.services;

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

public class ILocaleService implements LocaleService {

	final SynapsePlugin plugin;
	private final Locale systemOrDefault;
	private final Map<String, Class<? extends Translation>> defaultReferences = new HashMap<>();
	public ILocaleService(SynapsePlugin plugin) {
		this.plugin = plugin;
		systemOrDefault = EnumLocales.isValisTag(Locale.getDefault().toLanguageTag()) ? Locale.getDefault() : Locales.DEFAULT;
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

	@Override
	public <T extends Translation> LocalesList<T> createLocales(PluginContainer container) {
		return null;
	}

	@Override
	public <T extends Translation> LocalesList<T> createLocales(PluginContainer container, Class<? extends T> translationReference) {
		return null;
	}

	@Override
	public <T extends Translation> LocalesList<T> getLocales(PluginContainer container) {
		return null;
	}

	@Override
	public <T extends Translation> LocalesList<T> getLocales(String plugin) {
		return null;
	}

	@Override
	public boolean localesExist(PluginContainer container) {
		return false;
	}

	@Override
	public boolean localesExist(String plugin) {
		return false;
	}

}
