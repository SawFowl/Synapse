package sawfowl.synapse.api.services;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.text.callback.Callback;

/**
 * This service allows you to create clickable messages with the activation of your code.<br>
 * You cannot force the client or another server to execute your code in this way.<br>
 * For convenience, use the {@link Callback} interface.
 * 
 * @author SawFowl
 */
public interface CallbackSevice {

	static CallbackSevice get() {
		return Synapse.getCallbackSevice();
	}

	String addExecutor(Consumer<CommandSource> consumer);

	String addExecutor(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest);

	String addOneTimeExecution(Consumer<CommandSource> consumer);

	String addOneTimeExecution(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest);

	Optional<Consumer<CommandSource>> getCallback(String uuid, CommandSource source);

}
