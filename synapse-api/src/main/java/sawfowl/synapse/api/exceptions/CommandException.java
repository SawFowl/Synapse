package sawfowl.synapse.api.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.velocitypowered.api.command.VelocityBrigadierMessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.util.ComponentMessageThrowable;
import sawfowl.synapse.api.utils.TextUtils;

/**
 * Thrown when an executed command raises an error or when execution of
 * the command failed.
 */
public class CommandException extends CommandSyntaxException implements ComponentMessageThrowable {

	public CommandException(Message message) {
		super(new SimpleCommandExceptionType(message), message);
		this.message = message == null ? Component.empty() : TextUtils.deserialize(message.getString());
	}

	public CommandException(Component message) {
		this(VelocityBrigadierMessage.tooltip(message));
		this.message = message;
	}

	private static final long serialVersionUID = 4626722485860074825L;

	@Nullable private Component message;

	@Override
	public @Nullable String getMessage() {
		final @Nullable Component message = this.componentMessage();
		return message == null ? null : PlainTextComponentSerializer.plainText().serialize(message);
	}

	/**
	 * Returns the text message for this exception, or null if nothing is
	 * present.
	 *
	 * @return The text for this message
	 */
	@Override
	public @Nullable Component componentMessage() {
		return this.message;
	}

	@Override
	public @Nullable String getLocalizedMessage() {
		return this.getMessage();
	}

}
