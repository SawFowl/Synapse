package sawfowl.synapse.api.commands.settings;

import java.util.Optional;

import net.kyori.adventure.builder.AbstractBuilder;
import sawfowl.synapse.api.services.BuilderService;

/**
 * These settings are used only if the player executes the command.
 * 
 * @author SawFowl
 */
public interface CommandSettings {

	static Builder builder() {
		return BuilderService.get().get(Builder.class);
	}

	/**
	 * Setting the command execution price.
	 */
	Optional<CommandPrice> getPrice();

	/**
	 * Command execution delay.<br>
	 * Specified in seconds.<br>
	 * <br>
	 * If you assign a delay to the command,then you need to take into account that the player may be offline or something else will change.
	 * Otherwise, you can get a completely unpredictable result.
	 * Use it with caution.
	 * <br><br>
	 * default 0
	 */
	long getDelay();

	/**
	 * Command execution cooldown.<br>
	 * The player will not be able to execute the command again until the time runs out.<br>
	 * Specified in seconds.
	 * <br><br>
	 * default 0
	 */
	long getCooldown();

	/**
	 * Permission for the player to ignore the delay when executing this command.
	 */
	String getIgnoreDelay();

	/**
	 * Permission for the player to ignore the cooldown when executing this command.
	 */
	String getIgnoreCooldown();

	interface Builder extends AbstractBuilder<CommandSettings> {

		Builder setPrice(CommandPrice price);

		Builder setDelay(long delay);

		Builder setCooldown(long cooldown);

		Builder setIgnoreDelay(String permission);

		Builder setIgnoreCooldown(String permission);

	}

}
