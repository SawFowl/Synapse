package sawfowl.synapse;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPreShutdownEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.config.locale.ReferencedLocale;
import sawfowl.synapse.configure.Config;
import sawfowl.synapse.configure.localization.LocaleConfig;
import sawfowl.synapse.implementapi.ISynapse;
import sawfowl.synapse.implementapi.InjectorAPI;
import sawfowl.synapse.implementapi.services.IConfigurationService;
import sawfowl.synapse.implementapi.services.ILocaleService;
import sawfowl.synapse.implementapi.services.ILoggerService;
import sawfowl.synapse.utils.WatchRunner;

@Plugin(id = "synapse", authors = {"SawFowl"})
public class SynapsePlugin {

	private static SynapsePlugin instance;
	private static ReferencedConfig<Config> config;
	private static Path configDir;
	private static PluginContainer container;
	private static LocalesList<LocaleConfig> locales;
	private static Logger logger;

	@Inject
	public SynapsePlugin(ProxyServer server, @DataDirectory Path dataDirectory, PluginContainer container) {
		instance = this;
		if(!dataDirectory.toFile().exists()) dataDirectory.toFile().mkdir();
		configDir = dataDirectory;
		SynapsePlugin.container = container;
		logger = ILoggerService.getInstance().createApacheLogger("Synapse");
		ILocaleService.createInstance(dataDirectory);
		File mainConfig = null;
		for(File file : dataDirectory.toFile().listFiles()) {
			if(!file.isDirectory() && file.getName().contains("Config")) {
				mainConfig = file;
				break;
			}
		}
		if(mainConfig != null) {
			ConfigTypes type = ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(mainConfig.getName()));
			config = IConfigurationService.getInstance()
				.createReferencedConfig(container, Config.class)
				.setPath(dataDirectory)
				.setName("Config")
				.setType(type)
				.build();
			if(!type.comparableType(getConfig().getConfigSettings(container).getType())) {
				ConfigurationNode node = config.getRootNode();
				config = IConfigurationService.getInstance()
					.createReferencedConfig(container, getConfig())
					.setPath(dataDirectory)
					.setName("Config")
					.setType(getConfig().getConfigSettings(container).getType())
					.build();
				try {
					config.getLoader().save(node);
				} catch (ConfigurateException e) {
					e.printStackTrace();
				}
				mainConfig.delete();
				node = null;
			}
		}
		mainConfig = null;
		locales = ILocaleService.getInstance().createLocales(container, LocaleConfig.class);
		if(!locales.contains(Locales.DEFAULT)) locales.createReferencedTranslation(ConfigTypes.YAML, Locales.DEFAULT, LocaleConfig.class);
		if(!locales.contains(Locales.RU_RU)) locales.createReferencedTranslation(ConfigTypes.YAML, Locales.RU_RU, LocaleConfig.createRu());
		if(config == null) config = IConfigurationService.getInstance()
				.createReferencedConfig(container, Config.class)
				.setPath(dataDirectory)
				.setName("Config")
				.setType(ConfigTypes.YAML)
				.build();
		if(getConfig().getLocalesSettings(container).isForcedUse()) {
			@SuppressWarnings("unchecked")
			List<ReferencedLocale<LocaleConfig>> copy = locales.stream().map(locale -> (ReferencedLocale<LocaleConfig>) locale).toList();
			WatchRunner.pause();
			copy.forEach(localeConfig -> {
				if(!localeConfig.getType().comparableType(getConfig().getLocalesSettings(container).getType())) {
					locales.remove(localeConfig.getLocale());
					localeConfig.getPath().toFile().delete();
					try {
						locales.createReferencedTranslation(getConfig().getLocalesSettings(container).getType(), localeConfig.getLocale(), localeConfig.get()).getLoader().save(localeConfig.getRootNode());
					} catch (ConfigurateException e) {
						e.printStackTrace();
					}
				}
			});
			WatchRunner.pause();
			copy = null;
		}
		new InjectorAPI(new ISynapse(server)).createInjector();
	}

	@Subscribe
	public void onStarted(ProxyInitializeEvent event) {
		WatchRunner.getInstance().enable();
		WatchRunner.getInstance().run();
	}

	@Subscribe
	public void onStop(ProxyPreShutdownEvent event) {
		WatchRunner.getInstance().stopWatch();
	}

	@Subscribe
	public void onStop(ProxyReloadEvent event) {
		reload();
	}

	public void reload() {
		config.load();
	}

	public static SynapsePlugin getInstance() {
		return instance;
	}

	public static Config getConfig() {
		return config != null ? config.get() : null;
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
		return locales;
	}

	public static Logger getLogger() {
		return logger;
	}

}
