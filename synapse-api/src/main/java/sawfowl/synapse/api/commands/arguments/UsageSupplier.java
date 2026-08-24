package sawfowl.synapse.api.commands.arguments;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;

/**
 * This interface allows you to replace the message about an argument that was not entered or incorrectly entered.<br>
 * This does not affect the indication of the argument position in the command tree.
 */
@FunctionalInterface
public interface UsageSupplier {

	/**
	 * Do not return `null`!
	 * 
	 * @param source - It is used to identify who entered the command. For example, to obtain information about their language.
	 * @return
	 */
	@NotNull Component get(CommandSource source);

}
