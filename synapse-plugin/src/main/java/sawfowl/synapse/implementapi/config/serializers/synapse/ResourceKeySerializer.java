package sawfowl.synapse.implementapi.config.serializers.synapse;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.synapse.api.ResourceKey;

public final class ResourceKeySerializer implements TypeSerializer<ResourceKey> {

	public static final ResourceKeySerializer INSTANCE = new ResourceKeySerializer();

	private ResourceKeySerializer(){}

	@Override
	public ResourceKey deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return ResourceKey.tryParse(node.getString());
	}

	@Override
	public void serialize(Type type, @Nullable ResourceKey key, ConfigurationNode node) throws SerializationException {
		node.set(String.class, key.asString());
	}

}
