package sawfowl.synapse.api.text;

import java.util.function.Consumer;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.PlaceholderService;
import sawfowl.synapse.api.services.PlaceholderService.DefaultPlaceholderKeys;

public interface Text {

	private static Builder builder() {
		return BuilderService.get().get(Builder.class);
	}

	static Text of(Component component) {
		return builder().fromComponent(component);
	}

	/**
	 * You can use the mini message format, json, or plain text with formatting codes using the &amp; symbol.
	 */
	static Text of(String string) {
		return builder().fromString(string);
	}

	/**
	 * Exiting from the editor and retrieve the {@link Component} object.
	 */
	Component get();

	/**
	 * Exiting from the editor and getting a string without any decoration or additional functionality.<br>
	 * Appropriate for sending a message to the console.
	 */
	String toPlain();

	/**
	 * Appends a component to this text.
	 */
	Text append(Component component);

	/**
	 * Appends a text to this text.
	 */
	Text append(Text text);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String key, String value);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String key, Component value);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String key, Text value);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String key, Object value);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String[] keys, String... values);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String[] keys, Object... values);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String[] keys, Component... values);

	/**
	 * This method works similarly to {@link String#replace(String, String)}
	 */
	Text replace(String[] keys, Text... values);

	/**
	 * Adding the execution of arbitrary code when you click on text.<br>
	 */
	Text createCallBack(Consumer<CommandSource> callback);

	/**
	 * Adding the execution of arbitrary code when you click on text.<br>
	 * It is used {@link #createCallBack(Consumer)}
	 */
	Text createCallBack(Runnable runnable);

	/**
	 * Adding the execution of arbitrary code when you click on text.<br>
	 * It is used {@link SpongeComponents#executeCallback(callback)}
	 */
	//Text createCallBack(Consumer<CommandCause> callback);

	/**
	 * Removing all decorations from the text.
	 */
	Text removeDecorations();

	default Text replace(DefaultPlaceholderKeys key, String value) {
		return replace(key.textKey(), value);
	}

	default Text replace(DefaultPlaceholderKeys key, Component component) {
		return replace(key.textKey(), component);
	}

	default Text replace(DefaultPlaceholderKeys key, Object object) {
		return replace(key.textKey(), object);
	}

	default <T> Text applyPlaceholders(T target, Component def) {
		return PlaceholderService.get().apply(this, target, def);
	}

	default <T> Text applyPlaceholders(Component def, Object... args) {
		return PlaceholderService.get().apply(this, def, args);
	}

	default <T> Text applySystemPlaceholders(Component def) {
		return PlaceholderService.get().applySystemPlaceholders(this, def);
	}

	public interface Builder extends AbstractBuilder<Text> {

		Text fromComponent(Component component);

		Text fromString(String string);

	}

}
