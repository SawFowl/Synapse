package sawfowl.synapse.api.text.callback;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Payload;

import sawfowl.synapse.api.services.CallbackSevice;

/**
 * An interface for creating click events on a message.
 * 
 * @author SawFowl
 */
public interface Callback {

	/**
	 * @param consumer - Your custom code that must be executed by click.
	 */
	static ClickEvent<Payload.Text> of(Consumer<CommandSource> consumer) {
		return ClickEvent.runCommand(CallbackSevice.get().addExecutor(consumer));
	}

	/**
	 * @param consumer - Your custom code that must be executed by click.
	 * @param ignoreTest - If the result is true, the code called on the click will not be executed.
	 */
	static ClickEvent<Payload.Text> of(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest) {
		return ClickEvent.runCommand(CallbackSevice.get().addExecutor(consumer, ignoreTest));
	}

	/**
	 * The code will be activated only once.
	 * 
	 * @param consumer - Your custom code that must be executed by click.
	 * @param ignoreTest - If the result is true, the code called on the click will not be executed.
	 */
	static ClickEvent<Payload.Text> ofOneTimeExecution(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest) {
		return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer, ignoreTest));
	}

	/**
	 * The code will be activated only once.
	 * 
	 * @param consumer - Your custom code that must be executed by click.
	 */
	static ClickEvent<Payload.Text> ofOneTimeExecution(Consumer<CommandSource> consumer) {
		return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer));
	}

	/**
	 * @param runnable - Your custom code that must be executed by click.
	 */
	static ClickEvent<Payload.Text> of(Runnable runnable) {
		return of(_ -> runnable.run());
	}

	/**
	 * The code will be activated only once.
	 * 
	 * @param runnable - Your custom code that must be executed by click.
	 */
	static ClickEvent<Payload.Text> ofOneTimeExecution(Runnable runnable) {
		return ofOneTimeExecution(_ -> runnable.run());
	}

}
