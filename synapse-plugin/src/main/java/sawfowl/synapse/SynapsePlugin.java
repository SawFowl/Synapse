package sawfowl.synapse;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import com.google.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPreShutdownEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.config.locale.ReferencedLocale;
import sawfowl.synapse.api.services.EconomyService;
import sawfowl.synapse.commands.Callback;
import sawfowl.synapse.commands.ProxyInfo;
import sawfowl.synapse.commands.Sudo;
import sawfowl.synapse.configure.Config;
import sawfowl.synapse.configure.localization.LocaleConfig;
import sawfowl.synapse.implementapi.ISynapse;
import sawfowl.synapse.implementapi.InjectorAPI;
import sawfowl.synapse.implementapi.config.locale.ILocalesList;
import sawfowl.synapse.implementapi.services.ICallbackService;
import sawfowl.synapse.implementapi.services.ICommandService;
import sawfowl.synapse.implementapi.services.IConfigurationService;
import sawfowl.synapse.implementapi.services.ILocaleService;
import sawfowl.synapse.implementapi.services.ILoggerService;
import sawfowl.synapse.utils.WatchRunner;

@Plugin(id = "synapse", authors = {"SawFowl"}, version = "1.0.0")
public class SynapsePlugin {

	private static SynapsePlugin instance;
	private static ReferencedConfig<Config> config;
	private static Path configDir;
	private static PluginContainer container;
	private static ILocalesList<LocaleConfig> locales;
	private static Logger logger;
	private static long serverStartedTime;

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
		locales = (ILocalesList<LocaleConfig>) ILocaleService.getInstance().createLocales(container, LocaleConfig.class);
		if(!locales.contains(Locales.DEFAULT)) locales.createReferencedTranslation(config == null ? ConfigTypes.GEYSER_YAML : getConfig().getLocalesSettings(container).getType(), Locales.DEFAULT, LocaleConfig.class);
		if(!locales.contains(Locales.RU_RU)) locales.createReferencedTranslation(config == null ? ConfigTypes.GEYSER_YAML : getConfig().getLocalesSettings(container).getType(), Locales.RU_RU, LocaleConfig.createRu());
		if(config != null) {
			List<Locale> locales = new ArrayList<>();
			SynapsePlugin.locales.forEach(locale -> {
				if(getConfig().getLocalesSettings(container).getType() != locale.getType()) locales.add(locale.getLocale());
			});
			locales.forEach(locale -> {
				var config = SynapsePlugin.locales.getAsReferenced(locale);
				SynapsePlugin.locales.getSimple(locale).getPath().toFile().delete();
				if(SynapsePlugin.locales.contains(locale)) SynapsePlugin.locales.remove(locale);
				SynapsePlugin.locales.createReferencedTranslation(getConfig().getLocalesSettings(container).getType(), locale, config);
			});
		} else config = IConfigurationService.getInstance()
				.createReferencedConfig(container, Config.class)
				.setPath(dataDirectory)
				.setName("Config")
				.setType(ConfigTypes.GEYSER_YAML)
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
		SynapseBrigadierCommand.builder("psudo", container)
			.canUse(source -> source.hasPermission(Permissions.SUDO))
			.setAliases("gsudo")
			.setExecutor(new Sudo())
			.setArguments(
				Argument.PLAYER,
				Argument.createGreedyString("SpoofChatInput", false)
			)
			.build()
			.register();
		SynapseBrigadierCommand.builder("callback", container)
			.setArguments(Argument.createString("CallbackId", false))
			.setExecutor(new Callback())
			.setChilds(
				SynapseBrigadierCommand.builder("page", container)
					.setArguments(Argument.createString("Page", false))
					.setExecutor(new Callback.Pagination())
					.build()
			)
			.build()
			.register();
		SynapseBrigadierCommand.builder("proxyinfo", container)
			.canUse(source -> source.hasPermission(Permissions.PROXYINFO))
			.setAliases("pinfo", "ginfo")
			.setExecutor(new ProxyInfo())
			.build()
			.register();
	}

	@Subscribe
	public void onStarted(ProxyInitializeEvent event) {
		WatchRunner.getInstance().enable();
		WatchRunner.getInstance().run();
		Synapse.getProxy().getScheduler().buildTask(this, () -> ICommandService.getInstance().clearLastUsage()).delay(1, TimeUnit.SECONDS).repeat(5, TimeUnit.SECONDS).schedule();
		Synapse.getProxy().getScheduler().buildTask(this, () -> ICallbackService.getInstance().clearOld()).delay(10, TimeUnit.SECONDS).repeat(1, TimeUnit.MINUTES).schedule();
		if(!Synapse.getInstance().getServiceProvider().isExist(EconomyService.class)) logger.warn(getLocales().getSystemAsReferenced().getLoggerMessages().getEconomyNotFound());
		serverStartedTime = System.currentTimeMillis();
	}

	@Subscribe
	public void onStop(ProxyPreShutdownEvent event) {
		WatchRunner.getInstance().stopWatch();
	}

	@Subscribe
	public void onStop(ProxyReloadEvent event) {
		reload();
	}

	@Subscribe
	public void onCommand(CommandExecuteEvent event) {
		if(!(event.getCommandSource() instanceof Player player)) return;
		ICommandService.getInstance().cancelingDelayedExecution(player, locales.getAsReferenced(player).getCommands().getExceptions().getDelayCancel());
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

	public static long getServerUptime() {
		return (System.currentTimeMillis() - serverStartedTime) / 1000;
	}

}
