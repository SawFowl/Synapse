package sawfowl.synapse.implementapi.services;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.brigadier.arguments.BoolArgumentType;

import com.velocitypowered.api.proxy.Player;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.services.CommandService;

public class ICommandService implements CommandService {

	private Map<String, SynapseBrigadierCommand> commands = new HashMap<>();
	private Map<String, Argument<?>> defaultArguments = new HashMap<>();
	public ICommandService() {}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Argument<T> getArgument(String command, String name) {
		if(command == null || !commands.containsKey(command)) return defaultArguments.containsKey(name) ? (Argument<T>) defaultArguments.get(name) : null;
		return Stream.of(commands.get(command).getArgumentsCollection().getArguments()).filter(arg -> arg.getName().equals(name)).findFirst().map(arg -> (Argument<T>) arg).orElse(null);
	}

	@Override
	public Optional<SynapseBrigadierCommand> getCommand(String alias) {
		return commands.containsKey(alias) ? Optional.ofNullable(commands.get(alias)) : Optional.empty();
	}

	public void register(SynapseBrigadierCommand command) {
		register(command.getCommand(), command);
		if(command.getAliases() != null) for(String alias : command.getAliases()) register(alias, command);
	}

	public void unregister(SynapseBrigadierCommand command) {
		unregister(command.getCommand());
		if(command.getAliases() != null) for(String alias : command.getAliases()) unregister(alias);
	}

	private void register(String alias, SynapseBrigadierCommand command) {
		if(commands.containsKey(alias)) throw new RuntimeException("The command '/" + alias + "' is already registered!");
		commands.put(alias, command);
	}

	private void unregister(String alias) {
		if(commands.containsKey(alias)) commands.remove(alias);
	}

	public void registerDefaultBuilders() {
		defaultArguments.put(
				"Player",
				Argument.<Player>builder()
					.setName("Player")
					.setSuggestionProvider((_, builder) -> {
						Synapse.getProxy().getAllPlayers().forEach(p -> builder.suggest(p.getUsername()));
						return builder.buildFuture();
					})
					.setArgumentParser(arg -> Synapse.getProxy().getAllPlayers().stream().filter(p -> p.getUsername().equals(arg.getResult().toString())).findFirst())
					.build()
			);
			defaultArguments.put(
				"Duration",
				Argument.<Duration>builder()
					.setName("Duration")
					.setArgumentParser(arg -> parseDuration(arg.getResult().toString()))
					.build()
			);
			defaultArguments.put(
				"Boolean",
				Argument.<Boolean>builder()
					.setName("Boolean")
					.setRequirement(_ -> true)
					.setSuggestionProvider((_, builder) -> {
						builder.suggest("true");
						builder.suggest("false");
						return builder.buildFuture();
					})
					.setType(BoolArgumentType.bool())
					.setArgumentParser(arg -> cast(arg.getResult()))
					.build()
			);
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
	private static <T> Optional<T> cast(Object object) {
		try {
			return (Optional<T>) Optional.ofNullable(object);
		} catch (Exception e) {
			return null;
		}
	}

}
