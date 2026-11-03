package sawfowl.synapse.implementapi.services;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.services.CommandService;
import sawfowl.synapse.implementapi.command.IBrigadierCommand;
import sawfowl.synapse.implementapi.command.argument.GenericArgumentBuilder;
import sawfowl.synapse.utils.DelayTimerTask;

public class ICommandService implements CommandService {

	public static ICommandService getInstance() {
		return (ICommandService) CommandService.get();
	}

	private final Map<String, SynapseBrigadierCommand> commands = new ConcurrentHashMap<>();
	private final Map<String, SynapseBrigadierCommand> commandsByAlias = new ConcurrentHashMap<>();
	private Map<String, Argument<?>> defaultArguments = new HashMap<>();
	private Map<String, Argument<?>> defaultOptArguments = new HashMap<>();
	public ICommandService() {
		registerDefaultBuilders();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Argument<T> getArgument(String name, boolean optional) {
		if(optional) {
			return defaultOptArguments.containsKey(name) ? (Argument<T>) defaultOptArguments.get(name) : null;
		}
		return defaultArguments.containsKey(name) ? (Argument<T>) defaultArguments.get(name) : null;
	}

	@Override
	public Optional<SynapseBrigadierCommand> getCommand(String alias) {
		var command = commands.get(alias);
		return Optional.ofNullable(command != null ? command : commandsByAlias.get(alias));
	}

	@Override
	public boolean cancelingDelayedExecution(Player player, Component message) {
		return DelayTimerTask.cancel(player, message);
	}

	public void register(SynapseBrigadierCommand command) {
		// Check every name before registering anything, so a conflict leaves no half-registered command.
		if(commands.containsKey(command.getCommand())) throw new RuntimeException("The command '/" + command.getCommand() + "' is already registered!");
		if(command.getAliases() != null) for(String alias : command.getAliases()) if(commandsByAlias.containsKey(alias)) throw new RuntimeException("The command '/" + alias + "' is already registered!");
		commands.put(command.getCommand(), command);
		if(command.getAliases() != null) for(String alias : command.getAliases()) commandsByAlias.put(alias, command);
	}

	public void unregister(SynapseBrigadierCommand command) {
		unregister(command.getCommand());
		if(command.getAliases() != null) for(String alias : command.getAliases()) unregister(alias);
	}

	public void clearLastUsage() {
		commands.values().forEach(command -> ((IBrigadierCommand) command).clearLastUsage());
	}

	private void unregister(String alias) {
		commands.remove(alias);
		commandsByAlias.remove(alias);
	}

	private void registerDefaultBuilders() {
		defaultArguments.put(
			"Player",
			GenericArgumentBuilder.<Player>builder()
				.setName("Player")
				.setArgumentParser(arg -> Synapse.getProxy().getPlayer(arg.getResult().toString()))
				.setVariants(false, _ -> Synapse.getProxy().getAllPlayers().stream().map(Player::getUsername).toArray(String[]::new))
				.build()
		);
		defaultArguments.put(
			"Server",
			GenericArgumentBuilder.<RegisteredServer>builder()
				.setName("Server")
				.setArgumentParser(arg -> Synapse.getProxy().getServer(arg.getResult().toString()))
				.setVariants(false, _ -> Synapse.getProxy().getAllServers().stream().map(s -> s.getServerInfo().getName()).toArray(String[]::new))
				.build()
		);
		defaultArguments.put(
			"Duration",
			GenericArgumentBuilder.<Duration>builder()
				.setName("Duration")
				.setArgumentParser((_, context) -> context.getArguments().containsKey("Duration") && parseDuration(context.getArguments().get("Duration").getResult().toString()).isPresent(), arg -> parseDuration(arg.getResult().toString()))
				.build()
		);
		defaultArguments.forEach((n, a) -> defaultOptArguments.put(n, cast(a).copy().setOptional()));
	}

	private Optional<Duration> parseDuration(String s) {
		s = s.toUpperCase();
		if (!s.contains("T")) {
			if (s.contains("D")) {
				if (s.contains("H") || s.contains("M") || s.contains("S")) {
					s = s.replace("D", "DT");
				}
			} else {
				if (s.startsWith("P")) {
					s = "PT" + s.substring(1);
				} else {
					s = "T" + s;
				}
			}
		}
		if (!s.startsWith("P")) {
			s = "P" + s;
		}
		try {
			return Optional.ofNullable(Duration.parse(s));
		} catch (final DateTimeParseException ex) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> GenericArgumentBuilder<CommandSource, ?, ?> cast(Argument<T> arg) {
		return (GenericArgumentBuilder<CommandSource, ?, ?>) arg;
	}

}
