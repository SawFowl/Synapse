package sawfowl.synapse.implementapi.command.argument;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.commands.arguments.BrigadierArgumentsCollection;

public class IBrigadierArgumentsCollection<S extends CommandSource> implements BrigadierArgumentsCollection<S> {

	private Map<String, Argument<?>> args = new HashMap<String, Argument<?>>();
	private Argument<?>[] arguments = {};
	private SynapseBrigadierCommand command;
	public IBrigadierArgumentsCollection(SynapseBrigadierCommand command, Argument<?>... arguments) {
		if(arguments != null) {
			this.arguments = arguments;
			for(Argument<?> arg : arguments) addArg(arg);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> parse(String key, CommandContext<CommandSource> context) {
		return args.containsKey(key) && ((GenericArgumentBuilder<S, ?, ?>) args.get(key)).getArgumentPredicate().test(command, context) ? (Optional<T>) args.get(key).parse(context) : Optional.empty();
	}

	@Override
	public Argument<?>[] getArguments() {
		return arguments;
	}

	@Override
	public @Nullable Argument<?> getArgument(String key) {
		return args.containsKey(key) ? args.get(key) : null;
	}

	private void addArg(Argument<?> arg) {
		if(args.containsKey(arg.getName())) throw new RuntimeException("");
		args.put(arg.getName(), arg);
	}

}
