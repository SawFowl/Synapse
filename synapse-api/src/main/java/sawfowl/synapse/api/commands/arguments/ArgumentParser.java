package sawfowl.synapse.api.commands.arguments;

import java.util.Optional;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;

import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.commands.SynapseBrigadierCommand;

@FunctionalInterface
public interface ArgumentParser<S extends CommandSource, T> {

	Optional<T> parse(ParsedArgument<S, ?> arg);

	interface Predicate {

		static final Predicate DEFAULT = (_, _) -> true;

		boolean test(SynapseBrigadierCommand command, CommandContext<CommandSource> context);

	}

}
