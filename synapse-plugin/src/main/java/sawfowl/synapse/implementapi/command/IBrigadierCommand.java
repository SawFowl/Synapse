package sawfowl.synapse.implementapi.command;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
import sawfowl.synapse.implementapi.command.argument.SimpleCommandArgument;
import sawfowl.synapse.implementapi.services.ICommandService;

public class IBrigadierCommand implements SynapseBrigadierCommand {

	public static Builder builder() {
		return new IBrigadierCommand().createBuilder();
	}

	private String command;
	private PluginContainer container;
	private String[] aliases = {};
	private ParameterizedExecutor executor;
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

	@SuppressWarnings("unchecked")
	private BrigadierCommand createBrigadierCommand() {
		var node = createNodeBuilder(command, context -> {
			try {
				executor.execute(IBrigadierCommand.this, context);
			} catch (CommandException e) {
				context.getSource().sendMessage(e.componentMessage());
			}
		});
		if(childs != null && childs.length > 0) for(IBrigadierCommand child : childs) node.then(child.createBrigadierCommand().getNode());
		if(argumentsCollection != null && argumentsCollection.getArguments() != null && argumentsCollection.getArguments().length > 0) {
			GenericArgumentBuilder<CommandSource, ?, ?> parent = null;
			GenericArgumentBuilder<CommandSource, ?, ?> first = null;
			for(Argument<?> arg : argumentsCollection.getArguments()) {
				if(first != null) {
					var next = ((GenericArgumentBuilder<CommandSource, ?, ?>) arg).copy().setCommand(executor);
					parent.then(next);
					parent = next;
					next = null;
				} else parent = first = ((GenericArgumentBuilder<CommandSource, ?, ?>) arg).copy().setCommand(executor);
			}
			node.then(first);
			parent = null;
			first = null;
		}
		return new BrigadierCommand(node);
	}

	private SimpleCommandArgument createNodeBuilder(String key, Consumer<CommandContext<CommandSource>> consumer) {
		return (SimpleCommandArgument) new SimpleCommandArgument(this, key, canUse, executor).executes(context -> {
			consumer.accept(context);
			return com.mojang.brigadier.Command.SINGLE_SUCCESS;
		});
	}

	private Builder createBuilder() {
		return new IBuilder();
	}

	private class IBuilder implements Builder {

		@Override
		public SynapseBrigadierCommand build() {
			if(executor == null && (childs == null || childs.length == 0)) throw new RuntimeException("An executor or subcommands have not been assigned to the '/" + command + "' command.");
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
