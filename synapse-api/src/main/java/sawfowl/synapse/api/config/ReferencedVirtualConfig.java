package sawfowl.synapse.api.config;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;

import com.google.gson.JsonElement;

public interface ReferencedVirtualConfig<T> extends VirtualConfig {

	ReferencedVirtualConfig<T> loadFromRaw(String rawData);

	@Nullable
	T convertFromJson(JsonElement element);

	@Nullable
	<E extends JsonElement> E toJson(Class<E> jsonClass);

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
