package sawfowl.synapse.implementapi.config.serializers.synapse;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.synapse.api.commands.settings.CommandPrice;
import sawfowl.synapse.api.commands.settings.CommandSettings;

public final class CommandSettingsSerializer implements TypeSerializer<CommandSettings> {

	public static final CommandSettingsSerializer INSTANCE = new CommandSettingsSerializer();

	private CommandSettingsSerializer(){}

	@Override
	public CommandSettings deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return CommandSettings.builder()
				.setPrice(node.node("Price").virtual() ? null : node.node("Price").get(CommandPrice.class))
				.setDelay(node.node("Delay").virtual() ? 0 : node.node("Delay").getLong())
				.setCooldown(node.node("Cooldown").virtual() ? 0 : node.node("Cooldown").getLong())
				.setIgnoreCooldown(node.node("IgnorePermission", "Cooldown").virtual() ? null : node.node("IgnorePermission", "Cooldown").getString())
				.setIgnoreDelay(node.node("IgnorePermission", "Delay").virtual() ? null : node.node("IgnorePermission", "Delay").getString())
				.build();
	}

	@Override
	public void serialize(Type type, @Nullable CommandSettings commandSettings, ConfigurationNode node) throws SerializationException {
		if(commandSettings.getPrice().isPresent()) node.node("Price").set(commandSettings.getPrice().get());
		node.node("Cooldown").set(commandSettings.getCooldown());
		node.node("Delay").set(commandSettings.getDelay());
		node.node("IgnorePermission", "Cooldown").set(commandSettings.getIgnoreCooldown());
		node.node("IgnorePermission", "Delay").set(commandSettings.getIgnoreDelay());
	}

}
