package sawfowl.synapse.api.services;

import java.util.Optional;
import java.util.function.Consumer;

import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.Synapse;

public interface CallbackSevice {

	static CallbackSevice get() {
		return Synapse.getCallbackSevice();
	}

	String addExecutor(Consumer<CommandSource> consumer);

	String addOneTimeExecution(Consumer<CommandSource> consumer);

	Optional<Consumer<CommandSource>> getCallback(String uuid, CommandSource source);

}
