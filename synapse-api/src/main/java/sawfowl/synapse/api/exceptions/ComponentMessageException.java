package sawfowl.synapse.api.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.util.ComponentMessageThrowable;

/**
 * A subclass of Exception that contains a rich message that is an instance of
 * {@link Component} rather than a String. This allows formatted and localized
 * exception messages.
 * 
 * @authors Sponge team
 */
public class ComponentMessageException extends Exception implements ComponentMessageThrowable {

	private static final long serialVersionUID = -5281221645176698853L;

	@Nullable private final Component message;

	/**
	 * Constructs a new {@link ComponentMessageException}.
	 */
	public ComponentMessageException() {
		this.message = null;
	}

	/**
	 * Constructs a new {@link ComponentMessageException} with the given message.
	 *
	 * @param message The detail message
	 */
	public ComponentMessageException(final @Nullable Component message) {
		this.message = message;
	}

	/**
	 * Constructs a new {@link ComponentMessageException} with the given message and
	 * cause.
	 *
	 * @param message The detail message
	 * @param throwable The cause
	 */
	public ComponentMessageException(final @Nullable Component message, final Throwable throwable) {
		super(throwable);
		this.message = message;
	}

	/**
	 * Constructs a new {@link ComponentMessageException} with the given cause.
	 *
	 * @param throwable The cause
	 */
	public ComponentMessageException(final Throwable throwable) {
		super(throwable);
		this.message = null;
	}

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
