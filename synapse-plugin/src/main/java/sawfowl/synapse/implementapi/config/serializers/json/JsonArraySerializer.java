package sawfowl.synapse.implementapi.config.serializers.json;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public final class JsonArraySerializer implements TypeSerializer<JsonArray> {

	public static final JsonArraySerializer INSTANCE = new JsonArraySerializer();

	private JsonArraySerializer(){}

	@Override
	public JsonArray deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return get(JsonElementSerializer.createFromNode(node));
	}

	@Override
	public void serialize(Type type, @Nullable JsonArray array, ConfigurationNode node) throws SerializationException {
		serializeJsonArray(array, node);
	}

	public static void serializeJsonArray(JsonArray array, ConfigurationNode node) throws SerializationException {
		if(!array.isEmpty()) node.setList(JsonElement.class, array.asList());
	}

	private JsonArray get(JsonElement element) {
		return element.isJsonArray() ? element.getAsJsonArray() : new JsonArray();
	}

}
