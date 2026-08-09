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
import com.velocitypowered.api.proxy.Player;

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
import sawfowl.synapse.api.utils.ThrowingConsumer;

/**
 * An interface for creating and registering commands with arguments that support custom serialization.
 * 
 * @author SawFowl
 */
public interface SynapseBrigadierCommand {

	static Builder builder(@NotNull String name, @NotNull PluginContainer container) {
		return BuilderService.get().get(Builder.class).setName(name).setPlugin(container);
	}

	String getCommand();

	PluginContainer getPlugin();

	String[] getAliases();

	ParameterizedExecutor getExecutor();

	BrigadierArgumentsCollection<CommandSource> getArgumentsCollection();

	SynapseBrigadierCommand register();

	SynapseBrigadierCommand unregister();

	/**
	 * Additional optional settings for the command.
	 */
	CommandSettings getSettings();

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

	default int fail() {
		return 0;
	}

	/**
	 * Code execution delay.<br>
	 * Automatically activates the command's execution fee if required by the command's settings.<br>
	 * You can use this with any part of your command execution code that suits you.
	 */
	void delay(Player player, String ignorePermission, ThrowingConsumer<ParameterizedExecutor, CommandException> consumer) throws CommandException;

	/**
	 * Payment for the execution of the command.<br>
	 * You can use this with any part of your command execution code that suits you.
	 */
	void economy(Player player, String ignoreEconomyPermission) throws CommandException;

	@FunctionalInterface
	interface ParameterizedExecutor {

		/**
		 * 
		 * @param command - This command
		 * @param context - The context of the command execution.
		 * @return Use {@link SynapseBrigadierCommand#fail()} if the command fails, or {@link SynapseBrigadierCommand#success()} if it succeeds. This affects the cooldown between executions.
		 * @throws CommandException A text exception when executing the command, which will be shown to the {@link CommandSource}.
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

		/*default ClickEvent<Payload.Text> callbackSingle(Consumer<CommandSource> consumer, Predicate<CommandSource> ignoreTest) {
			return ClickEvent.runCommand(CallbackSevice.get().addOneTimeExecution(consumer, ignoreTest));
		}*/

		default ClickEvent<Payload.Text> callback(Runnable runnable) {
			return Callback.of(runnable);
		}

		/**
		 * Creating an exception when executing a command.
		 */
		default CommandSyntaxException exception(String message) {
			return new CommandException(new LiteralMessage(message));
		}

		/**
		 * Creating an exception when executing a command.
		 */
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

		Builder setSettings(CommandSettings settings);

	}

}
