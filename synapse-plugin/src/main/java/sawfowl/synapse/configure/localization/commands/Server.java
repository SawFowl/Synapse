package sawfowl.synapse.configure.localization.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.config.locale.Translation;

@ConfigSerializable
public class Server implements Translation {

	private static final String[] PLACEHOLDERS = {"%player%", "%server%"};

	public static Server createRu() {
		Server server = new Server();
		server.success = server.deserialize("&aВыполняется попытка подключения к серверу &e%server%&a.");
		server.successTarget = server.deserialize("&aВыполняется попытка подключения игрока &e%player%&a к серверу &e%server%&a.");
		return server;
	}

	public Server(){}

	@Setting("Success")
	private Component success = deserialize("&aAn attempt is being made to connect to server &e%server%&a.");
	@Setting("SuccessTarget")
	private Component successTarget = deserialize("&aAn attempt is being made to connect player &e%player%&a to server &e%server%&a.");

	public Component getSuccess(String server) {
		return replace(success, PLACEHOLDERS[1], server);
	}

	public Component getSuccessTarget(String player, String server) {
		return replace(successTarget, PLACEHOLDERS, player, server);
	}

}
