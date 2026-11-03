package sawfowl.synapse.implementapi.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Payload;
import sawfowl.synapse.api.services.CallbackSevice;
import sawfowl.synapse.implementapi.text.callback.Executor;

public class ICallbackService implements CallbackSevice {

	private static ICallbackService instance;
	public static ICallbackService getInstance() {
		return instance;
	}

	private final Map<String, Executor> executors = new ConcurrentHashMap<>();
	private final Map<String, Executor> paginations = new ConcurrentHashMap<>();
	public ICallbackService() {
		instance = this;
	}

	@Override
	public String addExecutor(Consumer<CommandSource> consumer) {
		String uuid = UUID.randomUUID().toString();
		executors.put(uuid.toString(), new Executor(consumer));
		return "/callback " + uuid;
	}

	@Override
	public String addExecutor(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest) {
		String uuid = UUID.randomUUID().toString();
		executors.put(uuid.toString(), new Executor(consumer).setIgnoreTest(ignoreTest));
		return "/callback " + uuid;
	}

	@Override
	public String addOneTimeExecution(Consumer<CommandSource> consumer) {
		String uuid = UUID.randomUUID().toString();
		executors.put(uuid.toString(), new Executor(consumer).setSingle(true));
		return "/callback " + uuid;
	}

	@Override
	public String addOneTimeExecution(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest) {
		String uuid = UUID.randomUUID().toString();
		executors.put(uuid.toString(), new Executor(consumer).setSingle(true).setIgnoreTest(ignoreTest));
		return "/callback " + uuid;
	}

	@Override
	public Optional<Consumer<CommandSource>> getCallback(String uuid, CommandSource source) {
		Executor executor = executors.get(uuid);
		if(executor == null) return Optional.empty();
		return Optional.ofNullable(checkRemove(uuid, executor, source));
	}

	public void clearOld() {
		executors.entrySet().removeIf(entry -> entry.getValue().getCreated() + 600000 < System.currentTimeMillis());
		paginations.entrySet().removeIf(entry -> entry.getValue().getCreated() + 600000 < System.currentTimeMillis());
	}

	private Consumer<CommandSource> checkRemove(String uuid, Executor executor, CommandSource forTest) {
		// Test first: a one-time callback must not be used up by someone it ignores.
		if(executor.getIgnoreTest() != null && executor.getIgnoreTest().test(forTest)) return null;
		// remove(key, value) is atomic, so two quick clicks cannot both run a one-time callback.
		if(executor.isSingle() && !executors.remove(uuid, executor)) return null;
		return executor.getConsumer();
	}

	public Optional<Consumer<CommandSource>> getPagination(String uuid, CommandSource source) {
		return Optional.ofNullable(paginations.get(uuid)).map(Executor::getConsumer);
	}

	public ClickEvent<Payload.Text> paginationOf(Consumer<CommandSource> consumer) {
		return ClickEvent.runCommand(addPagination(consumer));
	}

	private String addPagination(Consumer<CommandSource> consumer) {
		String uuid = UUID.randomUUID().toString();
		paginations.put(uuid.toString(), new Executor(consumer));
		return "/callback page " + uuid;
	}

}
