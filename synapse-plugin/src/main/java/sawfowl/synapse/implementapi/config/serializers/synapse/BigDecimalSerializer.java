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
		return read(node);
	}

	@Override
	public void serialize(Type type, @Nullable BigDecimal obj, ConfigurationNode node) throws SerializationException {
		write(obj, node);
	}

	/**
	 * Reads the number from its text, not through {@code double}, so money keeps every digit.
	 * Numbers written as strings are accepted too.
	 */
	static BigDecimal read(ConfigurationNode node) throws SerializationException {
		String string = node.getString();
		if(string == null || string.isBlank()) return BigDecimal.ZERO;
		try {
			return new BigDecimal(string.trim());
		} catch (NumberFormatException e) {
			throw new SerializationException(node, BigDecimal.class, e);
		}
	}

	/**
	 * Writes a plain number while {@code double} holds it exactly (the old format), otherwise a string.
	 */
	static void write(@Nullable BigDecimal value, ConfigurationNode node) throws SerializationException {
		if(value == null) {
			node.raw(null);
			return;
		}
		double asDouble = value.doubleValue();
		if(Double.isFinite(asDouble) && BigDecimal.valueOf(asDouble).compareTo(value) == 0) {
			node.set(asDouble);
		} else node.set(value.toPlainString());
	}

}
