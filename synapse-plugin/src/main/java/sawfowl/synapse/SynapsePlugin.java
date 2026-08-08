package sawfowl.synapse;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.config.locale.ReferencedLocale;
import sawfowl.synapse.configure.Config;
import sawfowl.synapse.configure.localization.LocaleConfig;
import sawfowl.synapse.implementapi.ISynapse;
import sawfowl.synapse.implementapi.InjectorAPI;
import sawfowl.synapse.implementapi.config.locale.ILocalesList;
import sawfowl.synapse.implementapi.services.ICommandService;
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
	private static ILocalesList<LocaleConfig> locales;
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
		((ICommandService) Synapse.getCommandService()).registerDefaultBuilders();
		/*SynapseBrigadierCommand.builder("test", container)
		.setArguments(
			Argument.createString("TestStringArg", true, "Variant1", "Variant2", "Variant3", "Variant4", "Variant5"),
			Argument.createString("Test2StringArg", false, "2Variant1", "2Variant2", "2Variant3", "2Variant4", "2Variant5"),
			Argument.createString("Test3StringArg", false, "3Variant1", "3Variant2", "3Variant3", "3Variant4", "3Variant5"),
			Argument.createIntRange("TestIntArg", 0, 5)
		)
		.setAliases("testalias1", "testalias2", "testalias3")
		.setChilds(
			SynapseBrigadierCommand
			.builder("child", container)
			.setArguments(
				Argument.createString("TestStringArg", false, "Variant1", "Variant2", "Variant3", "Variant4", "Variant5"),
				Argument.createIntRange("TestIntArg", 0, 5)
			)
			.setExecutor((command, context) -> {
				context.getSource().sendPlainMessage("Команда выполнена. " + command.getClass().getName() + " " + context.getInput());
				context.getSource().sendPlainMessage("Введенный аргумент TestStringArg -> " + command.<String>getArgument(context, "TestStringArg").orElse("Ничего не введено"));
				context.getSource().sendPlainMessage("Введенный аргумент TestIntArg -> " + command.<Integer>getArgument(context, "TestIntArg").map(i -> i.toString()).orElse("Ничего не введено"));
				return command.success();
			})
			.build()
		)
		.setExecutor((command, context) -> {
			context.getSource().sendPlainMessage("Команда выполнена. " + command.getClass().getName() + " " + context.getInput());
			context.getSource().sendPlainMessage("Введенный аргумент TestStringArg -> " + command.<String>getArgument(context, "TestStringArg").orElse("Ничего не введено"));
			context.getSource().sendPlainMessage("Введенный аргумент TestIntArg -> " + command.<Integer>getArgument(context, "TestIntArg").map(i -> i.toString()).orElse("Ничего не введено"));
			context.getSource().sendPlainMessage("Введенный аргумент Test2StringArg -> " + command.<String>getArgument(context, "Test2StringArg").orElse("Ничего не введено"));
			context.getSource().sendPlainMessage("Введенный аргумент Test3StringArg -> " + command.<String>getArgument(context, "Test3StringArg").orElse("Ничего не введено"));
			return command.success();
		})
		.build().register();*/
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
