package sawfowl.synapse.configure.localization.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.config.locale.Translation;
import sawfowl.synapse.api.utils.TextUtils;
import sawfowl.synapse.configure.localization.Time;

@ConfigSerializable
public class Commands implements Translation {

	private static final String COMMAND = "%command%";
	private static final String TIME = "%time%";
	public static Commands createRu() {
		Commands commands = new Commands();
		commands.exceptions = Exceptions.createRu();
		commands.waitingForActivation = commands.deserialize("&eАктивация команды '%command%' через: &6%time%");
		return commands;
	}

	public Commands(){}

	@Setting("Exceptions")
	private Exceptions exceptions = Exceptions.createEn();
	@Setting("WaitingForActivation")
	private Component waitingForActivation = deserialize("&eActivating the '%command%' command via: &6%time%");

	public Exceptions getExceptions() {
		return exceptions;
	}

	public Component getWaitingForActivation(String command, long time, Time timeConfig) {
		return replace(waitingForActivation, new String[] {COMMAND, TIME}, Component.text(command), TextUtils.timeFormat(time, timeConfig.getDay(), timeConfig.getHour(), timeConfig.getMinute(), timeConfig.getSecond()));
	}

}
