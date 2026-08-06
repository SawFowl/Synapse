package sawfowl.synapse.implementapi.text.callback;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.velocitypowered.api.command.CommandSource;

public class Executor {

	private final Consumer<CommandSource> consumer;
	private final long created;
	private boolean single = false;
	private Predicate<CommandSource> ignoreTest;
	public Executor(Consumer<CommandSource> consumer) {
		this.consumer = consumer;
		created = System.currentTimeMillis();
	}

	public Consumer<CommandSource> getConsumer() {
		return consumer;
	}

	public long getCreated() {
		return created;
	}

	public boolean isSingle() {
		return single;
	}

	public Executor setSingle(boolean single) {
		this.single = single;
		return this;
	}

	public Predicate<CommandSource> getIgnoreTest() {
		return ignoreTest;
	}

	public Executor setIgnoreTest(Predicate<CommandSource> ignoreTest) {
		this.ignoreTest = ignoreTest;
		return this;
	}

}
