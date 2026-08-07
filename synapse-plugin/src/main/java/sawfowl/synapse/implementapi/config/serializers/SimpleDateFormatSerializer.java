package sawfowl.synapse.implementapi.config.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class SimpleDateFormatSerializer implements TypeSerializer<SimpleDateFormat> {

	public static final SimpleDateFormatSerializer INSTANCE = new SimpleDateFormatSerializer();

	private SimpleDateFormatSerializer(){}

	@Override
	public SimpleDateFormat deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return new SimpleDateFormat(node.getString());
	}

	@Override
	public void serialize(Type type, @Nullable SimpleDateFormat format, ConfigurationNode node) throws SerializationException {
		node.set(format.toPattern());
	}

}
