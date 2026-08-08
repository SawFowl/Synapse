package sawfowl.synapse.api.commands.arguments;

import java.util.Optional;

import com.mojang.brigadier.context.ParsedArgument;
import com.velocitypowered.api.command.CommandSource;

@FunctionalInterface
public interface ArgumentParser<S extends CommandSource, T> {

	Optional<T> parse(ParsedArgument<S, ?> arg);

}
