package sawfowl.synapse.implementapi.config.serializers.synapse;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.synapse.api.ResourceKey;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.settings.CommandPrice;
import sawfowl.synapse.api.services.EconomyService;

public final class CommandPriceSerializer implements TypeSerializer<CommandPrice> {

	public static final CommandPriceSerializer INSTANCE = new CommandPriceSerializer();

	private CommandPriceSerializer(){}

	@Override
	public CommandPrice deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return CommandPrice.of(
			Synapse.getInstance().getServiceProvider().find(EconomyService.class).map(eco -> eco.getCurrency(ResourceKey.tryParse(node.node("Currency").getString())).orElse(null)).orElse(null),
			BigDecimal.valueOf(node.node("Money").getDouble()),
			node.node("IgnorePermission").getString());
	}

	@Override
	public void serialize(Type type, @Nullable CommandPrice price, ConfigurationNode node) throws SerializationException {
		node.node("Currency").set(String.class, price.getCurrency().getKey().asString());
		node.node("Money").set(String.class, price.getPrice().doubleValue());
		node.node("IgnorePermission").set(String.class, price.getIgnorePermission());
	}

}
