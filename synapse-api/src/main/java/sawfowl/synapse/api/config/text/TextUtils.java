package sawfowl.synapse.api.config.text;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class TextUtils {

	/**
	 * Adding the execution of arbitrary code when you click on text.<br>
	 * It is used {@link #createCallBack(Component, Consumer)}
	 */
	/*public static Component createCallBack(Component component, Runnable runnable) {
		return createCallBack(component, cause -> {
			runnable.run();
		});
	}

	/**
	 * Adding the execution of arbitrary code when you click on text.<br>
	 * It is used {@link SpongeComponents#executeCallback(callback)}
	 */
/*	public static Component createCallBack(Component component, Consumer<CommandCause> callback) {
		return component.clickEvent(SpongeComponents.executeCallback(callback));
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
		while(string.indexOf('&') != -1 && !string.endsWith("&") && isStyleChar(string.charAt(string.indexOf("&") + 1))) string = string.replaceAll("&" + string.charAt(string.indexOf("&") + 1), "");
		return string;
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
		if(isLegacyDecor(string)) {
			return deserializeLegacy(string);
		} else try {
			return deserializeJson(string);
		} catch (Exception e) {
			return deserializeLegacy(string);
		}
	}

	/**
	 * Checking the string for decorations.
	 */
	public static boolean isLegacyDecor(String string) {
		return string.indexOf('&') != -1 /*&& !string.endsWith("&")*/ && isStyleChar(string.charAt(string.indexOf("&") + 1));
	}


	/**
	 * Time formatting.
	 */
	public static Component timeFormat(long timeSecond, Locale locale, Component day, Component hour, Component minute, Component second) {
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

	private static boolean isStyleChar(char ch) {
		return "0123456789abcdefklmnor".indexOf(ch) != -1;
	}

}
