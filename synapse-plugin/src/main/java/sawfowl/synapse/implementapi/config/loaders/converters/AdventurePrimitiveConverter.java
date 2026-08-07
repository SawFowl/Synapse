package sawfowl.synapse.implementapi.config.loaders.converters;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.*;

/**
 * Converts Adventure objects to primitive types and back
 */
public final class AdventurePrimitiveConverter {

	private AdventurePrimitiveConverter() {}

	/**
	 * Checks if an object is an Adventure type
	 */
	public static boolean isAdventure(Object object) {
		return object instanceof Component 
			|| object instanceof NamedTextColor 
			|| object instanceof TextColor 
			|| object instanceof TextDecoration 
			|| object instanceof Key
			|| object instanceof ClickEvent
			|| object instanceof ClickEvent.Action
			|| object instanceof HoverEvent
			|| object instanceof HoverEvent.Action;
	}

	/**
	 * Converts Adventure object to a primitive type
	 */
	public static Object convertAdventureToPrimitive(Object value) {
		if(value == null) return null;

		if(value instanceof Component component) {
			return GsonComponentSerializer.gson().serialize(component);
		}

		if(value instanceof NamedTextColor color) {
			return color.toString();
		}

		if(value instanceof TextColor color) {
			return color.asHexString();
		}

		if(value instanceof TextDecoration decoration) {
			return decoration.toString();
		}

		if(value instanceof Key key) {
			return key.asString();
		}

		if(value instanceof ClickEvent.Action action) {
			return action.toString();
		}

		if(value instanceof ClickEvent clickEvent) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("action", clickEvent.action().toString());
			
			Object payload = clickEvent.payload();
			if(payload instanceof Component) {
				map.put("value", GsonComponentSerializer.gson().serialize((Component) payload));
			} else {
				map.put("value", payload.toString());
			}
			return map;
		}

		if(value instanceof HoverEvent.Action<?> action) {
			return action.toString();
		}

		if(value instanceof HoverEvent<?> hoverEvent) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("action", hoverEvent.action().toString());
			
			Object content = hoverEvent.value();
			if(content instanceof Component) {
				map.put("value", GsonComponentSerializer.gson().serialize((Component) content));
			} else {
				map.put("value", content.toString());
			}
			return map;
		}

		return value;
	}

	/**
	 * Recursively converts all Adventure objects in a Map to primitive types
	 * Supports maps with any key type (converts keys to strings)
	 */
	public static Map<String, Object> convertMapValuesToPrimitive(Map<?, ?> map) {
		Map<String, Object> result = new LinkedHashMap<>();
		for(Map.Entry<?, ?> entry : map.entrySet()) {
			String key = entry.getKey().toString();
			Object value = entry.getValue();
			result.put(key, convertValueToPrimitive(value));
		}
		return result;
	}

	/**
	 * Converts a value to a primitive type (recursive)
	 */
	public static Object convertValueToPrimitive(Object value) {
		if(value == null) return null;

		if(isAdventure(value)) {
			value = convertAdventureToPrimitive(value);
		}

		if(value instanceof Character character) {
			return character.toString();
		}

		if(value instanceof UUID uuid) {
			return uuid.toString();
		}

		if(value instanceof Map) {
			return convertMapValuesToPrimitive((Map<?, ?>) value);
		}

		if(value instanceof List) {
			List<Object> result = new ArrayList<>();
			for(Object item : (List<?>) value) {
				result.add(convertValueToPrimitive(item));
			}
			return result;
		}

		if(value instanceof Object[]) {
			Object[] array = (Object[]) value;
			Object[] result = new Object[array.length];
			for(int i = 0; i < array.length; i++) {
				result[i] = convertValueToPrimitive(array[i]);
			}
			return result;
		}

		return value;
	}
}