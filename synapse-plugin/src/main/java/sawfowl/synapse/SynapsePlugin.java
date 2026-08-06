package sawfowl.synapse;

import java.nio.file.Path;

import com.google.inject.Inject;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.services.ConfigurationService;
import sawfowl.synapse.api.services.LocaleService;
import sawfowl.synapse.configure.Config;
import sawfowl.synapse.configure.localization.LocaleConfig;
import sawfowl.synapse.implementapi.ISynapse;
import sawfowl.synapse.implementapi.InjectorAPI;

@Plugin(id = "synapse", authors = {"SawFowl"})
public class SynapsePlugin {

	private static SynapsePlugin instance;
	private static ReferencedConfig<Config> config;
	private static Path configDir;
	private static PluginContainer container;
	private static LocalesList<LocaleConfig> localesList;

	@Inject
	public SynapsePlugin(ProxyServer server, @DataDirectory Path dataDirectory, PluginContainer container) {
		instance = this;
		configDir = dataDirectory;
		SynapsePlugin.container = container;
		new InjectorAPI(new ISynapse(instance, server)).createInjector();
		localesList = LocaleService.get().createLocales(container, LocaleConfig.class);
		if(!localesList.contains(Locales.DEFAULT)) localesList.createReferencedTranslation(ConfigTypes.YAML, Locales.DEFAULT, new LocaleConfig());
		if(!localesList.contains(Locales.RU_RU)) localesList.createReferencedTranslation(ConfigTypes.YAML, Locales.RU_RU, LocaleConfig.createRu());
		config = ConfigurationService.get().createReferencedConfig(container, Config.class).setPath(dataDirectory).setName("Config").setType(ConfigTypes.YAML).build();
	}

	public void reload() {
		config.load();
	}

	public static SynapsePlugin getInstance() {
		return instance;
	}

	public static Config getConfig() {
		return config.get();
	}

	public static String getSynapsePluginConfigDir() {
		return configDir.toFile().getAbsolutePath();
	}

	public static String getMainConfigDir() {
		return configDir.getParent().toFile().getAbsolutePath();
	}

	public static PluginContainer getPluginContainer() {
		return container;
	}

	public static LocalesList<LocaleConfig> getLocales() {
		return localesList;
	}

}
