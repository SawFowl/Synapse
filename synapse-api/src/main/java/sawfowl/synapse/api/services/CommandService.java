package sawfowl.synapse.api.services;

import java.util.Optional;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;

/**
 * A service for interacting with commands created through the Synapse API.
 * 
 * @author SawFowl
 */
public interface CommandService {

	static CommandService get() {
		return Synapse.getCommandService();
	}

	/**
	 * Search for a command registered via the Synapse API.
	 * 
	 * @param alias
	 */
	Optional<SynapseBrigadierCommand> getCommand(String alias);

	/**
	 * Getting one of the pre-prepared Synapse arguments.<br>
	 * You don't need to use this method, see these arguments in the {@link Argument} interface.
	 */
	<T> Argument<T> getArgument(String name, boolean optional);

	/**
	 * Canceling the delayed execution of a command by a player.<br>
	 * By default, Synapse performs this operation only if the player has entered another command.<br>
	 * You can cancel the execution of the command when the player moves, but for this you will have to work with the network. For example, using the PacketEvents API.
	 * 
	 * @param player - The {@link Player} for whom you need to cancel the delayed execution of the command.
	 * @param message - A message sent to the player upon successful cancellation. The message may contain the %command% placeholder, which will be replaced with the cancelled command.
	 * @return <b>true</b> if cancellation is completed or <b>false</b> if there is nothing to cancel or the {@link Player} object = null.
	 */
	boolean cancelingDelayedExecution(Player player, Component message);

}
