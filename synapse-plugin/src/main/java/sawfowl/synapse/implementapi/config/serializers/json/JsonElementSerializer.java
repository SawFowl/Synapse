package sawfowl.synapse.implementapi.config.serializers.json;

import java.lang.reflect.Type;
import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import sawfowl.synapse.implementapi.services.IConfigurationService;

public final class JsonElementSerializer implements TypeSerializer<JsonElement> {

	public static final JsonElementSerializer INSTANCE = new JsonElementSerializer();

	private JsonElementSerializer(){}

	@Override
	public JsonElement deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return createFromNode(node);
	}

	@Override
	public void serialize(Type type, @Nullable JsonElement element, ConfigurationNode node) throws SerializationException {
		if(element.isJsonObject()) {
			JsonObjectSerializer.serializeJsonObject(element.getAsJsonObject(), node);
		} else if(element.isJsonArray()) {
			JsonArraySerializer.serializeJsonArray(element.getAsJsonArray(), node);
		} else if(element.isJsonPrimitive()) {
			JsonPrimitiveSerializer.serializeJsonPrimitive(element.getAsJsonPrimitive(), node);
		} else if(element.isJsonNull()) {
			Collection<Object> childs = node.childrenList().isEmpty() ? node.childrenMap().keySet() : node.childrenList().stream().map(child -> child.key()).toList();
			if(!childs.isEmpty()) childs.forEach(node::removeChild);
			childs = null;
		}
	}

	public static JsonElement createFromNode(ConfigurationNode node) {
		try {
			return JsonParser.parseString(toJsonString(findJsonObject(createGsonNode().from(node))));
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return new JsonObject();
	}

	private static ConfigurationNode findJsonObject(ConfigurationNode node) {
		if(node.key() != null && node.key().equals("SerializedJsonObject")) {
			try {
				if(node.isList()) {
					node.parent().setList(String.class, node.childrenList().stream().map(childNode -> toJsonString(childNode)).toList());
				} else node.parent().set(createFromNode(node).toString());
			} catch (ConfigurateException | JsonSyntaxException e) {
				e.printStackTrace();
			}
		} else if(node.isMap()) {
			node.childrenMap().values().forEach(childNode -> findJsonObject(childNode));
		} else if(node.isList()) {
			node.childrenList().forEach(childNode -> findJsonObject(childNode));
		}
		return node;
	}

	private static String toJsonString(ConfigurationNode node) {
		return new Gson().toJson(node.raw());
	}

	private static ConfigurationNode createGsonNode() {
		return GsonConfigurationLoader.builder().defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(IConfigurationService.getInstance().getSerializers()))).build().createNode();
	}

}
