package sawfowl.synapse.api.commands.arguments;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;

@FunctionalInterface
public interface ArgumentSupplier {

	String[] get(CommandContext<CommandSource> context);

}
