package sawfowl.synapse.implementapi.logger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.utils.TextUtils;

/** * 
 * @apiNote Most terminals would work with this, some terminals won't
 */
class Colors {

	public static final String CLEAR = "\u001B[0m";

	public static String convertColors(Object object) {
		if(object instanceof Component component) return convertColors(component);
		if(object instanceof String string) return convertColors(string);
		if(object == null) return "null";
		if(object instanceof Text text) return convertColors(text);
		if(object instanceof Optional<?> optional) return convertColors(optional);
		if(object instanceof Collection<?> collection) return convertColors(collection);
		if(object instanceof Map<?, ?> map) return convertColors(map);
		return convertColors(TextUtils.deserialize(object.toString()));
	}

	public static String convertColors(String string) {
		return convertColors(TextUtils.deserialize(string));
	}

	public static String convertColors(Text text) {
		return convertColors(text.get());
	}

	/**
	 * The text of the component and all its children, each part in its color.<br>
	 * The text of a component that has children used to be dropped, and children lost the color of their parent.
	 */
	public static String convertColors(Component message) {
		if(message == null) return "";
		StringBuilder builder = new StringBuilder();
		append(message, null, builder);
		return builder.toString();
	}

	private static void append(Component component, TextColor inherited, StringBuilder builder) {
		TextColor color = component.color() != null ? component.color() : inherited;
		String text = component.children().isEmpty() ? TextUtils.clearDecorations(component) : TextUtils.clearDecorations(component.children(List.of()));
		if(!text.isEmpty()) {
			if(color == null) {
				builder.append(text);
			} else builder.append(getAnsiColor(color)).append(text).append(CLEAR);
		}
		for(Component child : component.children()) append(child, color, builder);
	}

	private static <T, C extends Collection<T>> String convertColors(C list) {
		return list.isEmpty() ? list.toString() : list.stream().map(o -> convertColors(o)).toList().toString();
	}

	private static <K, V, C extends Map<K, V>> String convertColors(C map) {
		if(map.isEmpty()) return map.toString();
		// toMap() threw on keys that look the same once converted.
		Map<String, String> converted = new LinkedHashMap<>();
		map.forEach((key, value) -> converted.put(convertColors(key), convertColors(value)));
		return converted.toString();
	}

	private static String convertColors(Optional<?> optional) {
		return optional.isEmpty() ? optional.toString() : optional.map(o -> convertColors(o)).toString();
	}

	private static String getAnsiColor(TextColor color) {
		return selectColor(color.red(), color.green(), color.blue());
	}

	/**
	 * 
	 * selects a color using rgb/hex and converts to ANSI code
	 * 
	 * @param r - Red value (in decimal)
	 * @param g - Green value (in decimal)
	 * @param b - Blue value (in decimal)
	 * @return escape string with set rgb value
	 */
	private static String selectColor(int r, int g, int b) {
		if (r <= 255 && g <= 255 && b <= 255 && r >= 0 && g >= 0 && b >= 0)
			return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
		else
			return "\u001B[38;2;255;255;255m";
	}

	/**
	 * 
	 * selects a color using rgb/hex and converts to ANSI code
	 * 
	 * @param hexValue - color in hex value (don't include the '#')
	 * @return escape string with set rgb value (defaults to white if invalid hex
	 *         value)
	 */
	public static String selectColor(String hexValue) {
		if(hexValue == null || !hexValue.matches("[0-9a-fA-F]{6}")) return "\u001B[38;2;255;255;255m";
		int rgb = Integer.parseInt(hexValue, 16);
		return selectColor((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}

}
