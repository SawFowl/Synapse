package sawfowl.synapse.configure.localization.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.config.locale.Translation;

@ConfigSerializable
public class Tell implements Translation {

	private static final String[] PLACEHOLDERS = {"%player%", "%message%"};
	public static Tell createRu() {
		Tell tell = new Tell();
		tell.success = tell.deserialize("&5Вы шепчете &e%player%&f: &d%message%");
		tell.successTarget = tell.deserialize("&e%player% &5шепчет вам&f: &d%message%");
		return tell;
	}

	public Tell(){}

	@Setting("Success")
	private Component success = deserialize("&5You whisper &e%player%&f: &d%message%");
	@Setting("SuccessTarget")
	private Component successTarget = deserialize("&e%player% &5whispers to you&f: &d%message%");

	public Component getSuccess(Component target, Component message) {
		return replace(success, PLACEHOLDERS, target, message);
	}

	public Component getSuccessTarget(Component source, Component message) {
		return replace(successTarget, PLACEHOLDERS, source, message);
	}

}
