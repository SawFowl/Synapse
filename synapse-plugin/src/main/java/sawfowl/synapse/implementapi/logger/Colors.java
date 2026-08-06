package sawfowl.synapse.implementapi.logger;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.text.TextUtils;

/** * 
 * @apiNote Most terminals would work with this, some terminals won't
 */
class Colors {

	public static final String CLEAR = "\u001B[0m";

	public static String convertColors(Object object) {
		if(object instanceof Component component) return convertColors(component);
		if(object instanceof String string) return convertColors(string);
		if(object instanceof Optional optional) return convertColors(optional);
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

	public static String convertColors(Component message) {
		if(message == null) return "";
		if(!message.children().isEmpty()) {
			String result = "";
			for(Component component : message.children()) result += convertColors(component);
			return result + CLEAR;
		} else return message.color() == null ? TextUtils.clearDecorations(message) : getAnsiColor(message.color()) + TextUtils.clearDecorations(message) + CLEAR;
	}

	private static <T, C extends Collection<T>> String convertColors(C list) {
		return list.isEmpty() ? list.toString() : list.stream().map(o -> convertColors(o)).toList().toString();
	}

	private static <K, V, C extends Map<K, V>> String convertColors(C map) {
		return map.isEmpty() ? map.toString() : map.entrySet().stream().collect(Collectors.toMap(e -> convertColors(e.getKey()), e -> convertColors(e.getValue()))).toString();
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
		String rHex = "";
		String gHex = "";
		String bHex = "";
		if (hexValue.length() > 6 || !hexValue.matches("[0-9a-f]{6}$"))
			return "\u001B[38;2;255;255;255m";
		else {
			rHex = hexValue.substring(0, 2).toUpperCase();
			gHex = hexValue.substring(2, 4).toUpperCase();
			bHex = hexValue.substring(4, 6).toUpperCase();
		}
		// Convert to decimal
		BigInteger r = new BigInteger(rHex, 16);
		BigInteger g = new BigInteger(gHex, 16);
		BigInteger b = new BigInteger(bHex, 16);

		return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";

	}

}
