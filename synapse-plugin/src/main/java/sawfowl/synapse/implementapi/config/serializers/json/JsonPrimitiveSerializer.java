package sawfowl.synapse.implementapi.config.serializers.json;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.commons.lang3.math.NumberUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;

public final class JsonPrimitiveSerializer implements TypeSerializer<JsonPrimitive> {

	private static final TypeAdapter<JsonElement> STRICT_ADAPTER = new Gson().getAdapter(JsonElement.class);
	public static final JsonPrimitiveSerializer INSTANCE = new JsonPrimitiveSerializer();

	private JsonPrimitiveSerializer() {}

	@Override
	public JsonPrimitive deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return NumberUtils.isCreatable(node.raw().toString()) ?
			new JsonPrimitive(NumberUtils.createNumber(node.raw().toString())) :
			(isBoolean(node.raw().toString()) ?
				new JsonPrimitive(Boolean.valueOf(node.raw().toString())) :
				new JsonPrimitive(node.raw().toString())
			)
		;
	}

	@Override
	public void serialize(Type type, @Nullable JsonPrimitive primitive, ConfigurationNode node) throws SerializationException {
		serializeJsonPrimitive(primitive, node);
	}

	public static void serializeJsonPrimitive(JsonPrimitive primitive, ConfigurationNode node) throws SerializationException {
		if(primitive.isNumber()) {
			node.set(convertPrimitiveNumber(primitive.getAsNumber()));
		} else if(primitive.isBoolean()) {
			node.set(primitive.getAsBoolean());
		} else if(primitive.isString()) {
			if(isJsonString(primitive.getAsString())) {
				node.node("SerializedJsonObject").set(JsonElement.class, JsonParser.parseString(primitive.getAsString()));
			} else if(primitive.getAsString().length() == 1) {
				node.set(primitive.getAsString().charAt(0));
			} else node.set(primitive.getAsString());
		}
	}

	private static Number convertPrimitiveNumber(Number number) {
		return NumberUtils.createNumber(number.toString());
	}

	private static boolean isJsonString(String string) {
		try {
			STRICT_ADAPTER.fromJson(string);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean isBoolean(String string) {
		return string.equals("true") || string.equals("false");
	}

}
