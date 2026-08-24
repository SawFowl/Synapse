package sawfowl.synapse.api.commands.arguments;

import java.util.Optional;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;

import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.commands.SynapseBrigadierCommand;

/**
 * An interface for parsing your command argument.
 * 
 * @author SawFowl
 */
@FunctionalInterface
public interface ArgumentParser<S extends CommandSource, T> {

	/**
	 * Do not return null. In case of parsing failure, return {@link Optional#empty()}
	 */
	Optional<T> parse(ParsedArgument<S, ?> arg);

	/**
	 * This interface is auxiliary.<br>
	 * It allows you to set the conditions under which argument parsing will be possible.<br>
	 * You do not have to use it if you do not need to limit the parsing capabilities.
	 */
	@FunctionalInterface
	interface Predicate {

		static final Predicate DEFAULT = (_, _) -> true;

		boolean test(SynapseBrigadierCommand command, CommandContext<CommandSource> context);

	}

}
