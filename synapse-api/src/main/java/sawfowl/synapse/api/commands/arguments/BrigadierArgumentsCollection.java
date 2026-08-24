package sawfowl.synapse.api.commands.arguments;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;

/**
 * Each command has its own collection of arguments.
 * 
 * @author SawFowl
 */
public interface BrigadierArgumentsCollection<S extends CommandSource> {

	<T> Optional<T> parse(String key, CommandContext<CommandSource> context);

	Argument<?>[] getArguments();

	@Nullable Argument<?> getArgument(String key);

}
