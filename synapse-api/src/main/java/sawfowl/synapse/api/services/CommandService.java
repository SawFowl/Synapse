package sawfowl.synapse.api.services;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;

public interface CommandService {

	static CommandService get() {
		return Synapse.getCommandService();
	}

	Optional<SynapseBrigadierCommand> getCommand(String alias);

	<T> Argument<T> getArgument(@Nullable String command, String name);

	default <T> boolean isExistParser(@Nullable String command, String name) {
		return getArgument(command, name) != null;
	}

}
