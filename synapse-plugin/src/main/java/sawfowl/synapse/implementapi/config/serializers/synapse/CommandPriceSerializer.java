package sawfowl.synapse.implementapi.config.serializers.synapse;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.synapse.api.commands.settings.CommandPrice;
import sawfowl.synapse.implementapi.command.settings.ICommandPrice;

public final class CommandPriceSerializer implements TypeSerializer<CommandPrice> {

	public static final CommandPriceSerializer INSTANCE = new CommandPriceSerializer();

	private CommandPriceSerializer(){}

	/**
	 * The currency is looked up when the price is used, not here:
	 * Synapse loads its configs before any economy plugin registers its service,
	 * and a missing currency made {@code CommandPrice.of} throw and stopped Synapse from loading.
	 */
	@Override
	public CommandPrice deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return ICommandPrice.lazy(
			node.node("Currency").getString(),
			BigDecimalSerializer.read(node.node("Money")),
			node.node("IgnorePermission").getString());
	}

	@Override
	public void serialize(Type type, @Nullable CommandPrice price, ConfigurationNode node) throws SerializationException {
		if(price == null) {
			node.raw(null);
			return;
		}
		// set(String.class, double) failed with "Got a value of unexpected type", so a price could not be saved.
		node.node("Currency").set(price instanceof ICommandPrice iPrice ? iPrice.getCurrencyKey() : price.getCurrency().getKey().asString());
		BigDecimalSerializer.write(price.getPrice(), node.node("Money"));
		node.node("IgnorePermission").set(price.getIgnorePermission());
	}

}
