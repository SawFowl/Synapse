package sawfowl.synapse.api.config.builders;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.VirtualConfig;

public interface VirtualConfigBuilder {

	/**
	 * With this method, you can specify which configuration data to load from the raw string.<br>
	 * If you do not specify anything, the configuration will be empty and can be used to write any other data and then retrieve it as a raw string.
	 */
	VirtualConfigBuilder setData(String rawData);

	/**
	 * The type of the configuration.
	 */
	VirtualConfigBuilder setType(@NotNull ConfigTypes type);

	/**
	 * Additional serializers for your data.
	 */
	VirtualConfigBuilder addSerializers(TypeSerializerCollection collection);

	/**
	 * Creating configurations.
	 */
	VirtualConfig build();


}
