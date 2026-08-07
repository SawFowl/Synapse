package sawfowl.synapse.implementapi.config.serializers;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.synapse.api.config.ConfigTypes;

public final class ConfigTypeSerializer implements TypeSerializer<ConfigTypes> {

	public static final ConfigTypeSerializer INSTANCE = new ConfigTypeSerializer();

	private ConfigTypeSerializer(){}

	@Override
	public ConfigTypes deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return ConfigTypes.find(node.getString());
	}

	@Override
	public void serialize(Type type, @Nullable ConfigTypes configType, ConfigurationNode node) throws SerializationException {
		node.set(String.class, configType.getTypeName());
	}

}
