package sawfowl.synapse.api.commands;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.plugin.PluginContainer;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Payload;

import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.commands.arguments.BrigadierArgumentsCollection;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.CallbackSevice;
import sawfowl.synapse.api.text.callback.Callback;

public interface SynapseBrigadierCommand {

	static Builder builder(@NotNull String name, @NotNull PluginContainer container) {
		return BuilderService.get().get(Builder.class).setName(name).setPlugin(container);
	}

	String getCommand();

	PluginContainer getPlugin();

	String[] getAliases();

	ParameterizedExecutor getExecutor();

	BrigadierArgumentsCollection<CommandSource> getArgumentsCollection();

	void register();

	void unregister();

	default <T> Optional<T> getArgument(CommandContext<CommandSource> context, String key) {
		return getArgumentsCollection().parse(key, context);
	}

	default Optional<String> getStringArgument(CommandContext<CommandSource> context, String key) {
		return getArgument(context, key);
	}

	default Optional<Boolean> getBooleanArgument(CommandContext<CommandSource> context, String key) {
		return getArgument(context, key);
	}

	default Optional<Integer> getIntegerArgument(CommandContext<CommandSource> context, String key) {
		return getArgument(context, key);
	}

	default int success() {
		return Command.SINGLE_SUCCESS;
	}

	@FunctionalInterface
	interface ParameterizedExecutor {

		int execute(SynapseBrigadierCommand command,  CommandContext<CommandSource> context) throws CommandException;

		default Component withCallback(Component original, Consumer<CommandSource> consumer) {
			return original.clickEvent(callback(consumer));
		}

		default Component withCallback(Component original, Runnable runnable) {
			return original.clickEvent(callback(runnable));
		}

		default ClickEvent<Payload.Text> callback(Consumer<CommandSource> consumer) {
			return ClickEvent.runCommand(CallbackSevice.get().addExecutor(consumer));
		}

		default ClickEvent<Payload.Text> callbackSingle(Consumer<CommandSource> consumer) {
			return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer));
		}

		/*default ClickEvent<Payload.Text> callbackSingle(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest) {
			return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer, ignoreTest));
		}*/

		default ClickEvent<Payload.Text> callback(Runnable runnable) {
			return Callback.of(runnable);
		}

		default CommandSyntaxException exception(String message) {
			return new CommandException(new LiteralMessage(message));
		}

		default CommandSyntaxException exception(Component message) {
			return new CommandException(VelocityBrigadierMessage.tooltip(message));
		}

	}
	
	interface Builder extends AbstractBuilder<SynapseBrigadierCommand> {

		Builder setName(String name);

		Builder setPlugin(PluginContainer container);

		Builder setAliases(String... aliases);

		Builder setExecutor(ParameterizedExecutor executor);

		Builder setArguments(Argument<?>... arguments);

		Builder canUse(Predicate<CommandSource> canUse);

		Builder setChilds(SynapseBrigadierCommand... commands);

	}

}
