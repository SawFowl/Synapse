package sawfowl.synapse.implementapi.services;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

	private Map<String, SynapseBrigadierCommand> commands = new HashMap<>();
	private Map<String, SynapseBrigadierCommand> commandsByAlias = new HashMap<>();
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
		return commands.containsKey(alias) ? Optional.ofNullable(commands.get(alias)) : commandsByAlias.containsKey(alias) ? Optional.ofNullable(commandsByAlias.get(alias)) : Optional.empty();
	}

	@Override
	public boolean cancelingDelayedExecution(Player player, Component message) {
		return DelayTimerTask.cancel(player, message);
	}

	public void register(SynapseBrigadierCommand command) {
		if(commands.containsKey(command.getCommand())) throw new RuntimeException("The command '/" + command.getCommand() + "' is already registered!");
		commands.put(command.getCommand(), command);
		if(command.getAliases() != null) for(String alias : command.getAliases()) register(alias, command);
	}

	public void unregister(SynapseBrigadierCommand command) {
		unregister(command.getCommand());
		if(command.getAliases() != null) for(String alias : command.getAliases()) unregister(alias);
	}

	public void clearLastUsage() {
		commands.values().forEach(command -> ((IBrigadierCommand) command).clearLastUsage());
	}

	private void register(String alias, SynapseBrigadierCommand command) {
		if(commandsByAlias.containsKey(alias)) throw new RuntimeException("The command '/" + alias + "' is already registered!");
		commandsByAlias.put(alias, command);
	}

	private void unregister(String alias) {
		if(commands.containsKey(alias)) commands.remove(alias);
		if(commandsByAlias.containsKey(alias)) commandsByAlias.remove(alias);
	}

	private void registerDefaultBuilders() {
		defaultArguments.put(
			"Player",
			GenericArgumentBuilder.<Player>builder()
				.setName("Player")
				.setArgumentParser(arg -> Synapse.getProxy().getAllPlayers().stream().filter(p -> p.getUsername().equals(arg.getResult().toString())).findFirst())
				.setVariants(_ -> Synapse.getProxy().getAllPlayers().stream().map(Player::getUsername).toArray(String[]::new))
				.build()
		);
		defaultArguments.put(
			"Server",
			GenericArgumentBuilder.<RegisteredServer>builder()
				.setName("Server")
				.setArgumentParser(arg -> Synapse.getProxy().getAllServers().stream().filter(s -> s.getServerInfo().getName().equals(arg.getResult().toString())).findFirst())
				.setVariants(_ -> Synapse.getProxy().getAllServers().stream().map(s -> s.getServerInfo().getName()).toArray(String[]::new))
				.build()
		);
		defaultArguments.put(
			"Duration",
			GenericArgumentBuilder.<Duration>builder()
				.setName("Duration")
				.setArgumentParser(arg -> parseDuration(arg.getResult().toString()))
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
