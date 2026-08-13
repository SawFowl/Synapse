package sawfowl.synapse;

public class Permissions {

	public static final String SUDO = "synapse.command.sudo.use";
	public static final String SUDO_IGNORE = "synapse.command.sudo.ignore";

	private static final String IGNORE_COOLDOWN = "synapse.commands.ignore.cooldown.";

	public static String getIgnoreCooldown(String command) {
		return IGNORE_COOLDOWN + command;
	}

}
