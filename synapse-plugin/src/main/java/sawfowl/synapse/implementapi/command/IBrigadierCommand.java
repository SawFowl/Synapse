package sawfowl.synapse.implementapi.command;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.commands.arguments.BrigadierArgumentsCollection;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.services.CommandService;
import sawfowl.synapse.implementapi.command.argument.GenericArgumentBuilder;
import sawfowl.synapse.implementapi.command.argument.IBrigadierArgumentsCollection;
import sawfowl.synapse.implementapi.command.argument.CommandArgument;
import sawfowl.synapse.implementapi.services.ICommandService;

public class IBrigadierCommand implements SynapseBrigadierCommand {

	public static Builder builder() {
		return new IBrigadierCommand().createBuilder();
	}

	private String command;
	private PluginContainer container;
	private String[] aliases = {};
	private ParameterizedExecutor executor;
	private Command<CommandSource> brigadier;
	private BrigadierArgumentsCollection<CommandSource> argumentsCollection;
	private Predicate<CommandSource> canUse = _ -> true;
	private IBrigadierCommand[] childs;

	private IBrigadierCommand() {}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public PluginContainer getPlugin() {
		return container;
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public ParameterizedExecutor getExecutor() {
		return executor;
	}

	@Override
	public BrigadierArgumentsCollection<CommandSource> getArgumentsCollection() {
		return argumentsCollection;
	}

	@Override
	public void register() {
		if(brigadier == null && (childs == null || childs.length == 0)) return;
		Synapse.getProxy().getCommandManager().register(
			Synapse.getProxy().getCommandManager().metaBuilder(command).plugin(container).aliases(aliases).build(),
			createBrigadierCommand()
		);
		((ICommandService) CommandService.get()).register(this);
	}

	@Override
	public void unregister() {
		Synapse.getProxy().getCommandManager().unregister(command);
		((ICommandService) CommandService.get()).unregister(this);
	}

	private BrigadierCommand createBrigadierCommand() {
		var root = createNodeBuilder(command);
		if(canUse != null) root.requires(canUse);
		if(childs != null && childs.length > 0) for(IBrigadierCommand child : childs) root.then(child.createBrigadierCommand().getNode());
		if(argumentsCollection != null && argumentsCollection.getArguments() != null && argumentsCollection.getArguments().length > 0) {
			GenericArgumentBuilder<CommandSource, ?, ?> deepest = null;
			GenericArgumentBuilder<CommandSource, ?, ?> current = null;
			for(int i = argumentsCollection.getArguments().length - 1; i >= 0; i--) {
				Argument<?> arg = argumentsCollection.getArguments()[i];
				var builder = cast(arg).copy().setCommand(brigadier);
				if(deepest == null) {
					deepest = builder;
					current = builder;
				} else {
					builder.then(current);
					current = builder;
				}
			}
			root.then(current);
		}
		return new BrigadierCommand(root);
	}

	@SuppressWarnings("unchecked")
	private GenericArgumentBuilder<CommandSource, ?, ?> cast(Argument<?> arg) {
		return (GenericArgumentBuilder<CommandSource, ?, ?>) arg;
	}

	private CommandArgument createNodeBuilder(String key) {
		return (CommandArgument) new CommandArgument(key).executes(brigadier);
	}

	private Builder createBuilder() {
		return new IBuilder();
	}

	private boolean testArgsOnExecute(CommandContext<CommandSource> context) {
		for(Argument<?> arg : argumentsCollection.getArguments()) if(testArg(cast(arg), context)) return true;
		return false;
	}

	private boolean testArg(GenericArgumentBuilder<CommandSource, ?, ?> arg, CommandContext<CommandSource> context) {
		 if(!arg.isOprional() && !context.getArguments().containsKey(arg.getName())) {
				// TO DO: add message
			return true;
		}
		if(argumentsCollection.parse(arg.getName(), context).orElse(null) == null){
			// TO DO: add message
			return true;
		}
		return false;
	}

	private class IBuilder implements Builder {

		@Override
		public SynapseBrigadierCommand build() {
			if(executor == null) {
				if(childs == null || childs.length == 0) throw new RuntimeException("An executor or subcommands have not been assigned to the '/" + command + "' command.");
			} else if(argumentsCollection != null) brigadier = context -> {
				try {
					if(testArgsOnExecute(context)) return 0;
					return executor.execute(IBrigadierCommand.this, context);
				} catch (CommandException e) {
					context.getSource().sendMessage(e.componentMessage());
				}
				return success();
			};
			return IBrigadierCommand.this;
		}

		@Override
		public Builder setName(String name) {
			Objects.requireNonNull(name);
			IBrigadierCommand.this.command = name;
			return this;
		}

		@Override
		public Builder setPlugin(PluginContainer container) {
			Objects.requireNonNull(container);
			IBrigadierCommand.this.container = container;
			return this;
		}

		@Override
		public Builder setAliases(String... aliases) {
			IBrigadierCommand.this.aliases = aliases;
			return this;
		}

		@Override
		public Builder setExecutor(ParameterizedExecutor executor) {
			IBrigadierCommand.this.executor = executor;
			return this;
		}

		@Override
		public Builder setArguments(Argument<?>... arguments) {
			IBrigadierCommand.this.argumentsCollection = new IBrigadierArgumentsCollection<CommandSource>(arguments);
			return this;
		}

		@Override
		public Builder canUse(Predicate<CommandSource> canUse) {
			IBrigadierCommand.this.canUse = canUse;
			return this;
		}

		@Override
		public Builder setChilds(SynapseBrigadierCommand... commands) {
			IBrigadierCommand.this.childs = Stream.of(commands).map(command -> (IBrigadierCommand) command).toArray(IBrigadierCommand[]::new);
			return this;
		}

	}

}
