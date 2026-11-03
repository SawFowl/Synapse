package sawfowl.synapse.api.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.text.callback.Callback;

/**
 * @author SawFowl
 */
public class TextUtils {

	private static final Pattern LEGACY_CODE = Pattern.compile("&[0-9a-fk-or]");

	private static final MiniMessage PLAYER_MINI_MESSAGE = MiniMessage.builder()
		.tags(TagResolver.resolver(StandardTags.color(), StandardTags.decorations(), StandardTags.gradient(), StandardTags.rainbow(), StandardTags.reset()))
		.build();

	/**
	 * Adding the execution of arbitrary code when you click on text.<br>
	 * It is used {@link #createCallBack(Component, Consumer)}
	 */
	public static Component createCallBack(Component component, Runnable runnable) {
		return createCallBack(component, _ -> runnable.run());
	}

	/**
	 * Adding the execution of arbitrary code when you click on text.<br>
	 * It is used {@link Callback#of(callback)}
	 */
	public static Component createCallBack(Component component, Consumer<CommandSource> callback) {
		return component.clickEvent(Callback.of(callback));
	}

	/**
	 * It is used {@link LegacyComponentSerializer#legacyAmpersand()}
	 */
	public static final String serializeLegacy(Component component) {
		return component == null ? "" : LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

	/**
	 * It is used {@link GsonComponentSerializer#gson()}
	 */
	public static final String serializeJson(Component component) {
		return component == null ? "" : GsonComponentSerializer.gson().serialize(component);
	}

	/**
	 * It is used {@link LegacyComponentSerializer#legacyAmpersand()}
	 */
	public static final Component deserializeLegacy(String string) {
		return string == null ? Component.empty() : LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	/**
	 * It is used {@link GsonComponentSerializer#gson()}
	 */
	public static final Component deserializeJson(String string) {
		return string == null ? Component.empty() : GsonComponentSerializer.gson().deserialize(string);
	}

	public static final Component deserializeMiniMessage(String string) {
		try {
			return MiniMessage.miniMessage().deserialize(string);
		} catch (Exception e) {
			return deserializeLegacy(string);
		}
	}

	/**
	 * Removing all decorations from the text.
	 */
	public static final String clearDecorations(Component component) {
		return component == null ? "" : clearDecorations(PlainTextComponentSerializer.plainText().serialize(component));
	}

	/**
	 * Removing all decorations from the text.
	 */
	public static final String clearDecorations(String string) {
		if(string == null) return "";
		// Removes every "&<code>", not only the codes before the first "&" that is not a code.
		return LEGACY_CODE.matcher(string).replaceAll("");
	}

	/**
	 * Removing all decorations from the text.
	 */
	public static final Component removeDecorations(Component component) {
		return component == null ? Component.empty() : removeDecorations(serializeLegacy(component));
	}

	/**
	 * Removing all decorations from the text.
	 */
	public static final Component removeDecorations(String string) {
		return string == null ? Component.empty() : deserializeLegacy(clearDecorations(string));
	}

	/**
	 * String to {@link Component} conversion.
	 */
	public static final Component deserialize(String string) {
		if(string == null) return Component.empty();
		if(string.startsWith("<") && string.endsWith(">")) return deserializeMiniMessage(string);
		if(isLegacyDecor(string) || !looksLikeJson(string)) return deserializeLegacy(string);
		try {
			return deserializeJson(string);
		} catch (Exception e) {
			return deserializeLegacy(string);
		}
	}

	/**
	 * Text typed by a player (a private message, a chat line).<br>
	 * Only colors and decorations are allowed: legacy {@code &} codes or MiniMessage
	 * color/decoration/gradient/rainbow tags. Click, hover, insertion and other tags stay plain text,
	 * so a player cannot send someone a message that runs a command when clicked.
	 */
	public static final Component deserializePlayerInput(String string) {
		if(string == null) return Component.empty();
		if(isLegacyDecor(string)) return deserializeLegacy(string);
		try {
			return PLAYER_MINI_MESSAGE.deserialize(string);
		} catch (Exception e) {
			return Component.text(string);
		}
	}

	/**
	 * Checking the string for decorations.
	 */
	public static boolean isLegacyDecor(String string) {
		// Any "&<code>" in the string; the old check looked only at the first "&"
		// and threw StringIndexOutOfBoundsException when the string ended with it.
		return string != null && LEGACY_CODE.matcher(string).find();
	}

	/**
	 * Time formatting.
	 */
	public static Component timeFormat(long timeSecond, Component day, Component hour, Component minute, Component second) {
		long timeMinute = TimeUnit.SECONDS.toMinutes(timeSecond);
		long timeHour = TimeUnit.SECONDS.toHours(timeSecond);
		long timeDays = TimeUnit.SECONDS.toDays(timeSecond);
		if(timeDays == 0) {
			if(timeHour == 0) {
				if(timeMinute == 0) {
					return Text.of(String.format((timeSecond > 9 ? "%02d" : "%01d"), timeSecond) + "%second%").replace("%second%", second).get();
				} else return Text.of(String.format((timeMinute > 9 ? "%02d" : "%01d"), timeMinute) + "%minute%" + (timeSecond - (timeMinute * 60) > 0 ? " " + String.format((timeSecond - (timeMinute * 60) > 9 ? "%02d" : "%01d"), timeSecond - (timeMinute * 60)) + "%second%" : "")).replace(new String[] {"%minute%", "%second%"}, minute, second).get();
			} else return Text.of(String.format((timeHour > 9 ? "%02d" : "%01d"), timeHour) + "%hour%" + (timeMinute - (timeHour * 60) > 0 ? " " + String.format((timeMinute - (timeHour * 60) > 9 ? "%02d" : "%01d"), timeMinute - (timeHour * 60)) + "%minute%" : "")).replace(new String[] {"%hour%", "%minute%"}, hour, minute).get();
		}
		return Text.of(String.format((timeDays > 9 ? "%02d" : "%01d"), timeDays) + "%days% " + String.format((timeHour - (timeDays * 24) > 9 ? "%02d" : "%01d"), timeHour - (timeDays * 24)) + "%hour%" + (timeMinute - (timeHour * 60) > 0 ? " " + String.format((timeMinute - (timeHour * 60) > 9 ? "%02d" : "%01d"), timeMinute - (timeHour * 60)) + "%minute%" : "")).replace(new String[] {"%days%", "%hour%", "%minute%"}, day, hour, minute).get();
	}

	/**
	 * Only these can be valid JSON components; anything else is plain text,
	 * and parsing it as JSON would only cost an exception.
	 */
	private static boolean looksLikeJson(String string) {
		String trimmed = string.strip();
		return !trimmed.isEmpty() && (trimmed.charAt(0) == '{' || trimmed.charAt(0) == '[' || trimmed.charAt(0) == '"');
	}

}
