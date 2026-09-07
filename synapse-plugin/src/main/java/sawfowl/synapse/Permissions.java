package sawfowl.synapse;

public class Permissions {

	public static final String SUDO = "synapse.command.sudo.use";
	public static final String SUDO_IGNORE = "synapse.command.sudo.ignore";
	public static final String PROXYINFO = "synapse.command.proxyinfo";
	public static final String TELL = "synapse.command.tell";
	public static final String BROADCAST = "synapse.command.broadcast";
	public static final String SERVER = "synapse.command.server.use";
	public static final String SERVER_ADMIN = "synapse.command.server.admin";

	private static final String IGNORE_COOLDOWN = "synapse.commands.ignore.cooldown.";
	private static final String IGNORE_DELAY = "synapse.commands.ignore.delay.";
	private static final String SERVER_ARG = "synapse.commands.server.";

	public static String getIgnoreCooldown(String command) {
		return IGNORE_COOLDOWN + command;
	}

	public static String getIgnoreDelay(String command) {
		return IGNORE_DELAY + command;
	}

	public static String getServerArg(String server) {
		return SERVER_ARG + server;
	}

}
