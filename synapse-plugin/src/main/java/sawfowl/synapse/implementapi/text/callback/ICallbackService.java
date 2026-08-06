package sawfowl.synapse.implementapi.text.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.services.CallbackSevice;

public class ICallbackService implements CallbackSevice {

	private Map<String, Executor> executors = new HashMap<String, Executor>();
	public ICallbackService() {}

	@Override
	public String addExecutor(Consumer<CommandSource> consumer) {
		String uuid = UUID.randomUUID().toString();
		executors.put(uuid.toString(), new Executor(consumer));
		return "/callback execute " + uuid;
	}

	@Override
	public String addOneTimeExecution(Consumer<CommandSource> consumer) {
		String uuid = UUID.randomUUID().toString();
		executors.put(uuid.toString(), new Executor(consumer).setSingle(true));
		return "/callback execute " + uuid;
	}

	@Override
	public Optional<Consumer<CommandSource>> getCallback(String uuid, CommandSource source) {
		if(!executors.containsKey(uuid)) return Optional.empty();
		return Optional.ofNullable(checkRemove(uuid, executors.get(uuid), source));
	}

	private Consumer<CommandSource> checkRemove(String uuid, Executor executor, CommandSource forTest) {
		if(executor.isSingle()) executors.remove(uuid);
		return executor == null || (executor.getIgnoreTest() != null && executor.getIgnoreTest().test(forTest)) ? null : executor.getConsumer();
	}

}
