package sawfowl.synapse.configure.localization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.synapse.api.config.locale.Translation;
import sawfowl.synapse.configure.localization.commands.Commands;

@ConfigSerializable
public class LocaleConfig implements Translation {

	public static LocaleConfig createRu() {
		LocaleConfig config = new LocaleConfig();
		config.comments = ConfigComments.createRu();
		config.loggerMessages = LoggerMessages.createRu();
		config.commands = Commands.createRu();
		config.time = Time.createRu();
		return config;
	}

	public LocaleConfig(){}

	@Setting("ConfigComments")
	private ConfigComments comments = new ConfigComments();
	@Setting("LoggerMessages")
	private LoggerMessages loggerMessages = new LoggerMessages();
	@Setting("Commands")
	private Commands commands = new Commands();
	@Setting("Time")
	private Time time = new Time();

	public ConfigComments getComments() {
		return comments;
	}

	public LoggerMessages getLoggerMessages() {
		return loggerMessages;
	}

	public Commands getCommands() {
		return commands;
	}

	public Time getTime() {
		return time;
	}

}
