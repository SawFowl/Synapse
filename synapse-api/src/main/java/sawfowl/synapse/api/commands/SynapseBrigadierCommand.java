package sawfowl.synapse.api.commands;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Payload;

import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.commands.arguments.BrigadierArgumentsCollection;
import sawfowl.synapse.api.commands.settings.CommandSettings;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.CallbackSevice;
import sawfowl.synapse.api.text.callback.Callback;

/**
 * An interface for creating and registering commands with arguments that support custom serialization.
 * 
 * @author SawFowl
 */
public interface SynapseBrigadierCommand {

	static Builder builder(@NotNull String name, @NotNull PluginContainer container) {
		return BuilderService.get().get(Builder.class).setName(name).setPlugin(container);
	}

	/**
	 * The main alias of the command.
	 */
	String getCommand();

	/**
	 * The plugin that registered the command.
	 */
	PluginContainer getPlugin();

	/**
	 * Aliases of the command.
	 */
	String[] getAliases();

	/**
	 * The executor of the command.
	 */
	ParameterizedExecutor getExecutor();

	/**
	 * A collection of command arguments.
	 */
	BrigadierArgumentsCollection<CommandSource> getArgumentsCollection();

	/**
	 * Register this command.
	 */
	SynapseBrigadierCommand register();

	/**
	 * Cancel the registration of this command.
	 */
	SynapseBrigadierCommand unregister();

	/**
	 * Additional optional settings for the command.<br>
	 * These settings are used only if the player executes the command.
	 */
	CommandSettings getSettings();

	LiteralCommandNode<CommandSource> getCommandNode();

	/**
	 * Parsing an argument and getting its value.
	 * 
	 * @param <T> - The type of the argument object.
	 * @param context - The context of the command execution.
	 * @param key - Identifier of the argument.
	 * @return {@link Optional}, which, upon successful parsing, will contain an object with the specified type &ltT> . If parsing fails or if no argument has been entered, an empty {@link Optional} will be displayed.
	 */
	default <T> Optional<T> getArgument(CommandContext<CommandSource> context, String key) {
		return getArgumentsCollection().parse(key, context);
	}

	/**
	 * See {@link #getArgument}
	 */
	default Optional<String> getStringArgument(CommandContext<CommandSource> context, String key) {
		return getArgument(context, key);
	}

	/**
	 * See {@link #getArgument}
	 */
	default Optional<Boolean> getBooleanArgument(CommandContext<CommandSource> context, String key) {
		return getArgument(context, key);
	}

	/**
	 * See {@link #getArgument}
	 */
	default Optional<Integer> getIntegerArgument(CommandContext<CommandSource> context, String key) {
		return getArgument(context, key);
	}

	/**
	 * Use it when the command is executed successfully.
	 */
	default int success() {
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * Use it if the command is executed unsuccessfully.
	 */
	default int fail() {
		return 0;
	}

	/**
	 * Note that methods marked as default will not be available if you have registered the command executor via a lambda.
	 * If you need these methods, it is recommended to create classes that implement this interface.
	 * <br><br>
	 * Example lambda:
	 * <pre>
	 * (command, context) -> {
	 * 	// Your code
	 * 	return command.success();
	 * }
	 * </pre>
	 */
	@FunctionalInterface
	interface ParameterizedExecutor {

		/**
		 * @param command - This command
		 * @param context - The context of the command execution.
		 * @return Use {@link SynapseBrigadierCommand#fail()} if the command fails, or {@link SynapseBrigadierCommand#success()} if successful.
		 * This affects the cooldown between executions.
		 * @throws CommandException A text exception when executing the command, which will be shown to the {@link CommandSource}.<br>
		 * Any exception means that the command was executed unsuccessfully. This is equivalent to {@link SynapseBrigadierCommand#fail()}.
		 */
		int execute(SynapseBrigadierCommand command,  CommandContext<CommandSource> context) throws CommandException;

		/**
		 * Create clickable messages with the execution of your code.
		 */
		default Component withCallback(Component original, Consumer<CommandSource> consumer) {
			return original.clickEvent(callback(consumer));
		}

		/**
		 * Create clickable messages with the execution of your code.
		 */
		default Component withCallback(Component original, Runnable runnable) {
			return original.clickEvent(callback(runnable));
		}

		/**
		 * Create clickable messages with the execution of your code.
		 */
		default ClickEvent<Payload.Text> callback(Consumer<CommandSource> consumer) {
			return ClickEvent.runCommand(CallbackSevice.get().addExecutor(consumer));
		}

		/**
		 * Create clickable messages with the execution of your code.
		 */
		default ClickEvent<Payload.Text> callbackSingle(Consumer<CommandSource> consumer) {
			return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer));
		}

		/**
		 * Create clickable messages with the execution of your code.
		 */
		default ClickEvent<Payload.Text> callbackSingle(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest) {
			return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer, ignoreTest));
		}

		/**
		 * Create clickable messages with the execution of your code.
		 */
		default ClickEvent<Payload.Text> callback(Runnable runnable) {
			return Callback.of(runnable);
		}

		/**
		 * Creating an exception when executing a command.
		 */
		default CommandException exception(String message) throws CommandException {
			throw new CommandException(message);
		}

		/**
		 * Creating an exception when executing a command.
		 */
		default CommandException exception(Component message) throws CommandException {
			throw new CommandException(message);
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

		Builder setSettings(CommandSettings settings);

	}

}
