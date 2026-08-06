package sawfowl.synapse.api.text.callback;

import java.util.function.Consumer;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Payload;

import sawfowl.synapse.api.services.CallbackSevice;

public interface Callback {

	static ClickEvent<Payload.Text> of(Consumer<CommandSource> consumer) {
		return ClickEvent.runCommand(CallbackSevice.get().addExecutor(consumer));
	}

	static ClickEvent<Payload.Text> ofOneTimeExecution(Consumer<CommandSource> consumer) {
		return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer));
	}

	static ClickEvent<Payload.Text> of(Runnable runnable) {
		return of(_ -> runnable.run());
	}

	static ClickEvent<Payload.Text> ofOneTimeExecution(Runnable runnable) {
		return ofOneTimeExecution(_ -> runnable.run());
	}

}
