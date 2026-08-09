package sawfowl.synapse.implementapi.command;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.Permissions;
import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.commands.arguments.BrigadierArgumentsCollection;
import sawfowl.synapse.api.commands.settings.CommandSettings;
import sawfowl.synapse.api.economy.Currency;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.services.CommandService;
import sawfowl.synapse.api.services.EconomyService;
import sawfowl.synapse.api.utils.TextUtils;
import sawfowl.synapse.api.utils.ThrowingConsumer;
import sawfowl.synapse.implementapi.command.argument.GenericArgumentBuilder;
import sawfowl.synapse.implementapi.command.argument.IBrigadierArgumentsCollection;
import sawfowl.synapse.implementapi.command.settings.ICommandSettings;
import sawfowl.synapse.implementapi.command.argument.CommandArgument;
import sawfowl.synapse.implementapi.services.ICommandService;
import sawfowl.synapse.utils.DelayTimerTask;

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
	private IBrigadierCommand parrent;
	private CommandSettings settings = ICommandSettings.DEFAULT;
	private Map<UUID, UsedResult> lastUsed = new HashMap<>();

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
	public SynapseBrigadierCommand register() {
		if(brigadier == null && (childs == null || childs.length == 0)) return this;
		Synapse.getProxy().getCommandManager().register(
			Synapse.getProxy().getCommandManager().metaBuilder(command).plugin(container).aliases(aliases).build(),
			createBrigadierCommand()
		);
		((ICommandService) CommandService.get()).register(this);
		 return this;
	}

	@Override
	public SynapseBrigadierCommand unregister() {
		Synapse.getProxy().getCommandManager().unregister(command);
		((ICommandService) CommandService.get()).unregister(this);
		 return this;
	}

	@Override
	public CommandSettings getSettings() {
		return settings;
	}

	@Override
	public void delay(Player player, String ignorePermission, ThrowingConsumer<ParameterizedExecutor, CommandException> consumer) throws CommandException {
		if(settings.getDelayExecute() > 0 && !player.hasPermission(ignorePermission)) {
			Synapse.getProxy().getScheduler().buildTask(
				SynapsePlugin.getInstance(),
				new DelayTimerTask(consumer, player, container, command, this, ignorePermission)
			).repeat(1, TimeUnit.SECONDS).schedule();
		} else {
			economy(player, ignorePermission);
			consumer.accept(getExecutor());
		}
	}

	@Override
	public void economy(Player player, String ignoreEconomyPermission) throws CommandException {
		if(!Synapse.getInstance().getServiceProvider().isExist(EconomyService.class) || player.hasPermission(ignoreEconomyPermission)) return;
		var price = settings.getPrice().orElse(null);
		if(price == null || price.getPrice().doubleValue() < 0) return;
		var currency = price.getCurrency();
		var account = Synapse.getInstance().getServiceProvider().get(EconomyService.class).getOrCreateUniqueAccount(player);
		if(!account.hasBalance(currency) || account.getBalance(currency).doubleValue() < price.getPrice().doubleValue()) exceptionMoney(player.getEffectiveLocale(), currency, price.getPrice());
	
	}

	public void clearLastUsage() {
		lastUsed.entrySet().removeIf(entry -> (System.currentTimeMillis() / 1000) - settings.getCooldown() >= entry.getValue().time);
	}

	private CommandException exceptionMoney(Locale locale, Currency currency, BigDecimal money) throws CommandException {
		return new CommandException(SynapsePlugin.getLocales().getAsReferenced(locale).getCommands().getExceptions().getNoMoney(currency, money, command));
	}

	private BrigadierCommand createBrigadierCommand() {
		var root = createNodeBuilder(command);
		if(canUse != null) root.requires(canUse);
		if(childs != null && childs.length > 0) {
			for(var child : childs) {
				var childCommand = child.createBrigadierCommand();
				root.then(childCommand.getNode());
				if(child.getAliases() != null) for(String alias : child.getAliases()) {
					root.then(createAliasNode(alias, childCommand.getNode()));
				}
			}
		}
		if(argumentsCollection != null && argumentsCollection.getArguments() != null && argumentsCollection.getArguments().length > 0) {
			GenericArgumentBuilder<CommandSource, ?, ?> deepest = null;
			GenericArgumentBuilder<CommandSource, ?, ?> current = null;
			for(int i = argumentsCollection.getArguments().length - 1; i >= 0; i--) {
				var arg = argumentsCollection.getArguments()[i];
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

	private CommandNode<CommandSource> createAliasNode(String alias, CommandNode<CommandSource> originalNode) {
		var aliasBuilder = LiteralArgumentBuilder.<CommandSource>literal(alias);
		copyChildren(aliasBuilder, originalNode);
		if(originalNode.getRequirement() != null) aliasBuilder.requires(originalNode.getRequirement());
		return aliasBuilder.build();
	}

	private void copyChildren(LiteralArgumentBuilder<CommandSource> targetBuilder, CommandNode<CommandSource> sourceNode) {
		for(var child : sourceNode.getChildren()) {
			if(child instanceof LiteralCommandNode<CommandSource> literalChild) {
				var childBuilder = LiteralArgumentBuilder.<CommandSource>literal(literalChild.getName());
				if(literalChild.getCommand() != null) childBuilder.executes(literalChild.getCommand());
				if(literalChild.getRequirement() != null) childBuilder.requires(literalChild.getRequirement());
				copyChildren(childBuilder, literalChild);
				targetBuilder.then(childBuilder.build());
			} else if(child instanceof ArgumentCommandNode<CommandSource, ?> argChild) {
				@SuppressWarnings("unchecked")
				var type = (ArgumentType<Object>) argChild.getType();
				var argBuilder = RequiredArgumentBuilder.<CommandSource, Object>argument(argChild.getName(), type);
				if(argChild.getCommand() != null) argBuilder.executes(argChild.getCommand());
				if(argChild.getRequirement() != null) argBuilder.requires(argChild.getRequirement());
				if(argChild.getCustomSuggestions() != null) argBuilder.suggests(argChild.getCustomSuggestions());
				copyChildren(argBuilder, argChild);
				targetBuilder.then(argBuilder.build());
			}
		}
	}

	private void copyChildren(ArgumentBuilder<CommandSource, ?> targetBuilder, CommandNode<CommandSource> sourceNode) {
		for(var child : sourceNode.getChildren()) if(child instanceof LiteralCommandNode<CommandSource> literalChild) {
			var childBuilder = LiteralArgumentBuilder.<CommandSource>literal(literalChild.getName());
			if(literalChild.getCommand() != null) childBuilder.executes(literalChild.getCommand());
			if(literalChild.getRequirement() != null) childBuilder.requires(literalChild.getRequirement());
			copyChildren(childBuilder, literalChild);
			targetBuilder.then(childBuilder.build());
		} else if(child instanceof ArgumentCommandNode<CommandSource, ?> argChild) {
			@SuppressWarnings("unchecked")
			var type = (ArgumentType<Object>) argChild.getType();
			var argBuilder = RequiredArgumentBuilder.<CommandSource, Object>argument(argChild.getName(), type);
			if(argChild.getCommand() != null) argBuilder.executes(argChild.getCommand());
			if(argChild.getRequirement() != null) argBuilder.requires(argChild.getRequirement());
			if(argChild.getCustomSuggestions() != null) argBuilder.suggests(argChild.getCustomSuggestions());
			copyChildren(argBuilder, argChild);
			targetBuilder.then(argBuilder.build());
		}
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

	private boolean testArgsOnExecute(CommandContext<CommandSource> context, String input) {
		if(input.contains(" ")) {
			if(getRoot().childs != null) {
				input = updateInput(new StringBuilder(input.split(" ")[0]), input,getRoot().childs).toString();
			} else input = input.split(" ")[0];
		}
		if(!input.startsWith("/")) input = "/" + input;
		return testArgsOnExecute(context, new UsageComponentBuilder(input + " "));
	}

	private StringBuilder updateInput(StringBuilder builder, String original, IBrigadierCommand[] childs) {
		for(IBrigadierCommand child : childs) {
			if(original.startsWith(builder.toString() + " " + child.getCommand())) {
				builder.append(" " + child.getCommand());
				return child.childs != null ? updateInput(builder, original, child.childs) : builder;
			} else {
				for(String childAlias : child.aliases) {
					if(original.startsWith(builder.toString() + " " + childAlias)) {
						builder.append(" " + childAlias);
						return child.childs != null ? updateInput(builder, original, child.childs) : builder;
					}
				}
			}
		}
		return builder;
	}

	private boolean testArgsOnExecute(CommandContext<CommandSource> context, UsageComponentBuilder input) {
		for(Argument<?> arg : argumentsCollection.getArguments()) if(testArg(cast(arg), context, input)) return true;
		return false;
	}

	private boolean testArg(GenericArgumentBuilder<CommandSource, ?, ?> arg, CommandContext<CommandSource> context, UsageComponentBuilder usedAliasAndArgs) {
		if(!context.getArguments().containsKey(arg.getName())) {
			if(!arg.isOptional()) {
				usedAliasAndArgs.setFirst(arg.getUsage().get(context.getSource()).append(Component.newline())).append("&4↳<" + arg.getName() + ">↲");
				context.getSource().sendMessage(usedAliasAndArgs.component);
				return true;
			}
		} else if(!arg.isAllowed(context, context.getArguments().get(arg.getName()).getResult().toString())){
			usedAliasAndArgs.setFirst(arg.getUsage().get(context.getSource()).append(Component.newline())).append("&4↳<" + arg.getName() + ">↲");
			context.getSource().sendMessage(usedAliasAndArgs.component);
			return true;
		}
		usedAliasAndArgs.append(Component.text("[" + arg.getName() + "] "));
		return false;
	}

	private IBrigadierCommand setParrent(IBrigadierCommand command) {
		parrent = command;
		return this;
	}

	private IBrigadierCommand getRoot() {
		if(parrent != null) return parrent.getRoot();
		return this;
	}

	private class IBuilder implements Builder {

		@Override
		public SynapseBrigadierCommand build() {
			if(executor == null) {
				if(childs == null || childs.length == 0) throw new RuntimeException(SynapsePlugin.getLocales().getSystemAsReferenced().getLoggerMessages().getExecutorNotAssigned(command));
			} else if(argumentsCollection != null) brigadier = context -> {
				try {
					if(settings != null && settings.getCooldown() > 0 && context.getSource() instanceof Player player && !player.hasPermission(Permissions.getIgnoreCooldown(command))) {
						if(lastUsed.containsKey(player.getUniqueId())) {
							if((System.currentTimeMillis() / 1000) - settings.getCooldown() < lastUsed.get(player.getUniqueId()).time) {
								player.sendMessage(SynapsePlugin.getLocales().getAsReferenced(player).getCommands().getExceptions().getCooldown(settings.getCooldown() - ((System.currentTimeMillis() / 1000) - lastUsed.get(player.getUniqueId()).time), SynapsePlugin.getLocales().getAsReferenced(player).getTime()));
								return fail();
							} else lastUsed.remove(player.getUniqueId());
						}
						int result = executor.execute(IBrigadierCommand.this, context);
						if(result != 0) lastUsed.put(player.getUniqueId(), new UsedResult(result, System.currentTimeMillis() / 1000));
						return result;
					}
					if(testArgsOnExecute(context, context.getInput())) return 0;
					return executor.execute(IBrigadierCommand.this, context);
				} catch (CommandException e) {
					context.getSource().sendMessage(e.componentMessage());
					return fail();
				}
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
			Objects.requireNonNull(aliases);
			IBrigadierCommand.this.aliases = aliases;
			return this;
		}

		@Override
		public Builder setExecutor(ParameterizedExecutor executor) {
			Objects.requireNonNull(executor);
			IBrigadierCommand.this.executor = executor;
			return this;
		}

		@Override
		public Builder setArguments(Argument<?>... arguments) {
			Objects.requireNonNull(arguments);
			IBrigadierCommand.this.argumentsCollection = new IBrigadierArgumentsCollection<CommandSource>(arguments);
			return this;
		}

		@Override
		public Builder canUse(Predicate<CommandSource> canUse) {
			Objects.requireNonNull(canUse);
			IBrigadierCommand.this.canUse = canUse;
			return this;
		}

		@Override
		public Builder setChilds(SynapseBrigadierCommand... commands) {
			Objects.requireNonNull(commands);
			IBrigadierCommand.this.childs = Stream.of(commands).map(command -> ((IBrigadierCommand) command).setParrent(IBrigadierCommand.this)).toArray(IBrigadierCommand[]::new);
			return this;
		}

		@Override
		public Builder setSettings(CommandSettings settings) {
			Objects.requireNonNull(settings);
			IBrigadierCommand.this.settings = settings;
			return this;
		}

	}

	private record UsedResult(int result, long time) {}

	private class UsageComponentBuilder {

		Component component;
		UsageComponentBuilder(String input) {
			component = TextUtils.deserialize(input);
		}

		UsageComponentBuilder append(Component component) {
			this.component = this.component.append(component);
			return this;
		}

		UsageComponentBuilder append(String string) {
			this.component = this.component.append(TextUtils.deserialize(string));
			return this;
		}

		UsageComponentBuilder setFirst(Component component) {
			this.component = component.append(this.component);
			return this;
		}

	}


}
