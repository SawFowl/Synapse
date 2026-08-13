package sawfowl.synapse.configure.localization.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.config.locale.Translation;

@ConfigSerializable
public class Sudo implements Translation {

	private static final String PLAYER = "%player%";
	private static final String[] INPUT = {PLAYER, "%input%"};
	public static Sudo createRu() {
		Sudo sudo = new Sudo();
		sudo.fail = sudo.deserialize("&cИгрок '&e%player%&c' имеет иммунитет, его нельзя принудить ввести что либо в чат.");
		sudo.success = sudo.deserialize("&aВы принудили игрока '&e%player%&a' к вводу в чат '&e%input%&a'.");
		return sudo;
	}

	public Sudo(){}

	private Component fail = deserialize("&cThe player '&e%player%&c' is immune, he cannot be forced to enter anything into the chat.");
	private Component success = deserialize("&aYou forced the player '&e%player%&a' to enter the chat '&e%input%&a'.");

	public Component getFail(Player player) {
		return replace(fail, PLAYER, player.getUsername());
	}

	public Component getSuccess(Player player, String input) {
		return replace(success, INPUT, player.getUsername(), input);
	}

}
