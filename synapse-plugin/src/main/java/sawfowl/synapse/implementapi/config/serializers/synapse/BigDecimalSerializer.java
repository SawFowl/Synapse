package sawfowl.synapse.implementapi.config.serializers.synapse;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class BigDecimalSerializer implements TypeSerializer<BigDecimal> {

	public static final BigDecimalSerializer INSTANCE = new BigDecimalSerializer();

	@Override
	public BigDecimal deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return BigDecimal.valueOf(node.getDouble());
	}

	@Override
	public void serialize(Type type, @Nullable BigDecimal obj, ConfigurationNode node) throws SerializationException {
		node.set(obj.doubleValue());
	}

}
