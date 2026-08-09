package sawfowl.synapse.api.services;

import java.util.Optional;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;

public interface CommandService {

	static CommandService get() {
		return Synapse.getCommandService();
	}

	Optional<SynapseBrigadierCommand> getCommand(String alias);

	<T> Argument<T> getArgument(String name, boolean optional);

}
