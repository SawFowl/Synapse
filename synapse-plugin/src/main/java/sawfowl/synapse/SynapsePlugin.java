package sawfowl.synapse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
import sawfowl.synapse.commands.Broadcast;
import sawfowl.synapse.commands.Callback;
import sawfowl.synapse.commands.ProxyInfo;
import sawfowl.synapse.commands.Server;
import sawfowl.synapse.commands.Sudo;
import sawfowl.synapse.commands.Tell;
import sawfowl.synapse.configure.Commands;
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
import sawfowl.synapse.utils.DelayTimerTask;
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
	private ReferencedConfig<Commands> commands;

	@Inject
	public SynapsePlugin(ProxyServer server, @DataDirectory Path dataDirectory, PluginContainer container) {
		instance = this;
		if(!dataDirectory.toFile().exists()) dataDirectory.toFile().mkdir();
		configDir = dataDirectory;
		SynapsePlugin.container = container;
		logger = ILoggerService.getInstance().createApacheLogger("Synapse");
		ILocaleService.createInstance(dataDirectory);
		// Only "Config.<known extension>": a backup like "Config.yml.bak" used to be picked up
		// as an UNKNOWN type and stop the plugin from loading.
		File mainConfig = null;
		File[] files = dataDirectory.toFile().listFiles();
		if(files != null) for(File file : files) {
			if(file.isFile() && file.getName().startsWith("Config.") && ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(file.getName())) != ConfigTypes.UNKNOWN) {
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
		if(config == null) config = IConfigurationService.getInstance()
				.createReferencedConfig(container, Config.class)
				.setPath(dataDirectory)
				.setName("Config")
				.setType(ConfigTypes.GEYSER_YAML)
				.build();
		convertOwnLocales();
		new InjectorAPI(new ISynapse(server)).createInjector();
		commands = Synapse.getConfigurationService().createReferencedConfig(container, Commands.class).setName("Commands").setType(getConfig().getConfigSettings(container).getType()).setPath(dataDirectory).build();
		if(getCommands().getProxysudo().isEnable()) SynapseBrigadierCommand.builder("proxysudo", container)
			.canUse(source -> source.hasPermission(Permissions.SUDO))
			.setAliases(getCommands().getProxysudo().getAliases())
			.setSettings(getCommands().getProxysudo().getSettings())
			.setExecutor(new Sudo())
			.setArguments(
				Argument.PLAYER,
				Argument.createGreedyString("SpoofChatInput", false)
			)
			.build()
			.register();
		SynapseBrigadierCommand.builder("callback", container)
			.setArguments(Argument.createString("CallbackId", false, true))
			.setExecutor(new Callback())
			.setChilds(
				SynapseBrigadierCommand.builder("page", container)
					.setArguments(Argument.createString("Page", false, true))
					.setExecutor(new Callback.Pagination())
					.build()
			)
			.build()
			.register();
		if(getCommands().getProxyinfo().isEnable()) SynapseBrigadierCommand.builder("proxyinfo", container)
			.canUse(source -> source.hasPermission(Permissions.PROXYINFO))
			.setAliases(getCommands().getProxyinfo().getAliases())
			.setSettings(getCommands().getProxyinfo().getSettings())
			.setExecutor(new ProxyInfo())
			.build()
			.register();
		if(getCommands().getProxytell().isEnable()) SynapseBrigadierCommand.builder("proxytell", container)
			.canUse(source -> source.hasPermission(Permissions.TELL))
			.setAliases(getCommands().getProxytell().getAliases())
			.setSettings(getCommands().getProxytell().getSettings())
			.setChilds(
				SynapseBrigadierCommand.builder("console", container)
					.canUse(source -> source instanceof Player)
					.setSettings(getCommands().getProxytell().getSettings())
					.setArguments(
						Argument.createPlayerComponent("Message", false)
					)
					.setExecutor(new Tell.Console())
					.build()
			)
			.setArguments(
				Argument.PLAYER,
				Argument.createPlayerComponent("Message", false)
			)
			.setExecutor(new Tell())
			.build()
			.register();
		if(getCommands().getBroadcast().isEnable()) SynapseBrigadierCommand.builder("proxybroadcast", container)
			.canUse(source -> source.hasPermission(Permissions.BROADCAST))
			.setAliases(getCommands().getBroadcast().getAliases())
			.setSettings(getCommands().getBroadcast().getSettings())
			.setArguments(Argument.createComponent("Message", false))
			.setExecutor(new Broadcast())
			.build()
			.register();
		if(getCommands().getServer().isEnable()) SynapseBrigadierCommand.builder("server", container)
			.canUse(source -> source.hasPermission(Permissions.SERVER))
			.setAliases(getCommands().getServer().getAliases())
			.setSettings(getCommands().getServer().getSettings())
			.setArguments(Server.ARGUMENTS)
			.setExecutor(new Server())
			.build()
			.register();
	}

	@Subscribe
	public void onStarted(ProxyInitializeEvent event) {
		// run() enables the watcher itself.
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
		// Most commands run with nothing delayed: do not build the localized message for them.
		if(!(event.getCommandSource() instanceof Player player) || !DelayTimerTask.hasPending(player)) return;
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

	/**
	 * Synapse's own locales follow the format from its config.<br>
	 * There were two copies of this: one compared types with {@code !=}, so YAML and GeyserYaml
	 * files were rewritten on every start, and it did not pause the file watcher.
	 */
	@SuppressWarnings("unchecked")
	private void convertOwnLocales() {
		ConfigTypes target = getConfig().getLocalesSettings(container).getType();
		List<ReferencedLocale<LocaleConfig>> toConvert = locales.stream()
			.map(locale -> (ReferencedLocale<LocaleConfig>) locale)
			.filter(locale -> !locale.getType().comparableType(target))
			.toList();
		if(toConvert.isEmpty()) return;
		WatchRunner.pause();
		try {
			for(ReferencedLocale<LocaleConfig> old : toConvert) {
				ConfigurationNode node = old.getRootNode();
				locales.remove(old.getLocale());
				try {
					locales.createReferencedTranslation(target, old.getLocale(), old.get()).getLoader().save(node);
					// Deleted only after the new file is written.
					Files.deleteIfExists(old.getPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			WatchRunner.resume();
		}
	}

	public static long getServerUptime() {
		return (System.currentTimeMillis() - serverStartedTime) / 1000;
	}

	public Commands getCommands() {
		return commands.get();
	}

}
