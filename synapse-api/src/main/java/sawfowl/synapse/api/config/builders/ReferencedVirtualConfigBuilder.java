package sawfowl.synapse.api.config.builders;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedVirtualConfig;

public interface ReferencedVirtualConfigBuilder<T> {

	/**
	 * The type of the configuration.
	 */
	ReferencedVirtualConfigBuilder<T> setType(@NotNull ConfigTypes type);

	/**
	 * Additional serializers for your data.
	 */
	ReferencedVirtualConfigBuilder<T> addSerializers(TypeSerializerCollection collection);

	/**
	 * Creating configurations.
	 */
	ReferencedVirtualConfig<T>  build();

}
