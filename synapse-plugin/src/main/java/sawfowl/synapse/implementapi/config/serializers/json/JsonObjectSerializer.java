package sawfowl.synapse.implementapi.config.serializers.json;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;

public class JsonObjectSerializer implements TypeSerializer<JsonObject> {

	final TypeAdapter<JsonElement> strictAdapter = new Gson().getAdapter(JsonElement.class);
	public static final JsonObjectSerializer INSTANCE = new JsonObjectSerializer();

	private JsonObjectSerializer(){}

	@Override
	public JsonObject deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return get(JsonElementSerializer.createFromNode(node));
	}

	@Override
	public void serialize(Type type, @Nullable JsonObject obj, ConfigurationNode node) throws SerializationException {
		serializeJsonObject(obj, node);
	}

	public static void serializeJsonObject(JsonObject jsonObject, ConfigurationNode node) throws SerializationException {
		for(Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if(entry.getValue().isJsonPrimitive()) {
				node.node(entry.getKey()).set(JsonPrimitive.class, entry.getValue().getAsJsonPrimitive());
			} else if(entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().size() > 0) {
				node.node(entry.getKey()).set(JsonObject.class, entry.getValue().getAsJsonObject());
			} else if(entry.getValue().isJsonArray()) {
				node.node(entry.getKey()).set(JsonArray.class, entry.getValue().getAsJsonArray());
			}
		}
	}

	private JsonObject get(JsonElement element) {
		return element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
	}

}
