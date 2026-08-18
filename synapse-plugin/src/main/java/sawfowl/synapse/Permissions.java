package sawfowl.synapse;

public class Permissions {

	public static final String SUDO = "synapse.command.sudo.use";
	public static final String SUDO_IGNORE = "synapse.command.sudo.ignore";
	public static final String PROXYINFO = "synapse.command.proxyinfo";
	public static final String TELL = "synapse.command.tell";
	public static final String BROADCAST = "synapse.command.broadcast";

	private static final String IGNORE_COOLDOWN = "synapse.commands.ignore.cooldown.";
	private static final String IGNORE_DELAY = "synapse.commands.ignore.delay.";

	public static String getIgnoreCooldown(String command) {
		return IGNORE_COOLDOWN + command;
	}

	public static String getIgnoreDelay(String command) {
		return IGNORE_DELAY + command;
	}

}
