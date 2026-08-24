package sawfowl.synapse.api.commands.arguments;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;

/**
 * This interface is used to provide an array of strings for quick argument input.
 * 
 * @author SawFowl
 */
@FunctionalInterface
public interface ArgumentSupplier {

	String[] get(CommandContext<CommandSource> context);

}
