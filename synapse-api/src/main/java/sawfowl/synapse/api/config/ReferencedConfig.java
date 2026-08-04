package sawfowl.synapse.api.config;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;

public interface ReferencedConfig<T> extends Config {

	/**
	 * Getting the configuration loader.
	 */
	<N extends ConfigurationNode> ConfigurationReference<N> getReference();

	/**
	 * See {@link ValueReference}
	 */
	<N extends ConfigurationNode> ValueReference<T, N> getValueReference();

	/**
	 * Saving an object of the serializable class to the current configuration.
	 */
	<E extends T> void save(E object);

	/**
	 * Retrieving an object of the serializable class from the current configuration.
	 */
	default T get() {
		return getValueReference().get();
	}


}
