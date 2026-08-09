package sawfowl.synapse;

public class Permissions {

	private static final String IGNORE_COOLDOWN = "synapse.commands.ignore.cooldown.";

	public static String getIgnoreCooldown(String command) {
		return IGNORE_COOLDOWN + command;
	}

}
