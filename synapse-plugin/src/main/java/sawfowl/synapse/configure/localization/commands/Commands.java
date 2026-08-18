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
	private static final String MESSAGE = "%message%";
	public static Commands createRu() {
		Commands commands = new Commands();
		commands.exceptions = Exceptions.createRu();
		commands.sudo = Sudo.createRu();
		commands.proxyInfo = ProxyInfo.createRu();
		commands.tell = Tell.createRu();
		commands.waitingForActivation = commands.deserialize("&eАктивация команды '/%command%' через: &6%time%");
		commands.broadcast = commands.deserialize("&2Объявление&f: %message%");
		return commands;
	}

	public Commands(){}

	@Setting("Exceptions")
	private Exceptions exceptions = Exceptions.createEn();
	@Setting("Sudo")
	private Sudo sudo = new Sudo();
	@Setting("ProxyInfo")
	private ProxyInfo proxyInfo = new ProxyInfo();
	@Setting("Tell")
	private Tell tell = new Tell();
	@Setting("WaitingForActivation")
	private Component waitingForActivation = deserialize("&eActivating the '/%command%' command via: &6%time%");
	@Setting("Broadcast")
	private Component broadcast = deserialize("&2Broadcast&f: %message%");

	public Exceptions getExceptions() {
		return exceptions;
	}

	public Sudo getSudo() {
		return sudo;
	}

	public ProxyInfo getProxyInfo() {
		return proxyInfo;
	}

	public Tell getTell() {
		return tell;
	}

	public Component getWaitingForActivation(String command, long time, Time timeConfig) {
		return replace(waitingForActivation, new String[] {COMMAND, TIME}, Component.text(command), TextUtils.timeFormat(time, timeConfig.getDay(), timeConfig.getHour(), timeConfig.getMinute(), timeConfig.getSecond()));
	}

	public Component getBroadcast(Component message) {
		return replace(broadcast, MESSAGE, message);
	}

}
