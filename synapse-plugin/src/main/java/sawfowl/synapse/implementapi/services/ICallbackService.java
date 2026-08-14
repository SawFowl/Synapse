package sawfowl.synapse.implementapi.services;

import java.util.HashMap;
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

	private Map<String, Executor> executors = new HashMap<String, Executor>();
	private Map<String, Executor> paginations = new HashMap<String, Executor>();
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
		if(!executors.containsKey(uuid)) return Optional.empty();
		return Optional.ofNullable(checkRemove(uuid, executors.get(uuid), source));
	}

	public void clearOld() {
		executors.entrySet().removeIf(entry -> entry.getValue().getCreated() + 600000 < System.currentTimeMillis());
		paginations.entrySet().removeIf(entry -> entry.getValue().getCreated() + 600000 < System.currentTimeMillis());
	}

	private Consumer<CommandSource> checkRemove(String uuid, Executor executor, CommandSource forTest) {
		if(executor.isSingle()) executors.remove(uuid);
		return executor.getIgnoreTest() != null && executor.getIgnoreTest().test(forTest) ? null : executor.getConsumer();
	}

	public Optional<Consumer<CommandSource>> getPagination(String uuid, CommandSource source) {
		if(!paginations.containsKey(uuid)) return Optional.empty();
		return Optional.ofNullable(paginations.get(uuid).getConsumer());
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
