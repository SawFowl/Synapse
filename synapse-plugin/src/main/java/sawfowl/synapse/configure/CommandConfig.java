package sawfowl.synapse.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.synapse.Permissions;
import sawfowl.synapse.api.commands.settings.CommandSettings;

@ConfigSerializable
public class CommandConfig {

	public CommandConfig(){}
	public CommandConfig(String command, String... aliases) {
		this.aliases = aliases;
		settings = CommandSettings.builder().setIgnoreCooldown(Permissions.getIgnoreCooldown(command)).setIgnoreDelay(Permissions.getIgnoreDelay(command)).build();
	}

	@Setting("Settings")
	private CommandSettings settings;
	@Setting("Aliases")
	private String[] aliases;
	@Setting("Enable")
	private boolean enable = true;

	public CommandSettings getSettings() {
		return settings;
	}

	public String[] getAliases() {
		return aliases;
	}
	public boolean isEnable() {
		return enable;
	}

}
