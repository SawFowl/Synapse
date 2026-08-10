package sawfowl.synapse.api.services;

import java.nio.file.Path;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import com.google.gson.JsonObject;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.builders.ConfigBuilder;
import sawfowl.synapse.api.config.builders.ReferencedConfigBuilder;
import sawfowl.synapse.api.config.builders.ReferencedVirtualConfigBuilder;
import sawfowl.synapse.api.config.builders.VirtualConfigBuilder;

/**
 * @author SawFowl
 */
public interface ConfigurationService {

	static ConfigurationService get() {
		return Synapse.getConfigurationService();
	}

	/**
	 * Creating a simple configuration.
	 */
	public abstract ConfigBuilder createSimpleConfig(PluginContainer container);

	/**
	 * Creating a reference configuration that accepts and returns a serializable object of the specified type.<br>
	 * See also {@link ConfigSerializable}
	 */
	public abstract <T> ReferencedConfigBuilder<T> createReferencedConfig(PluginContainer container, Class<T> type);

	/**
	 * Creating a reference configuration that accepts and returns a serializable object of the specified type.<br>
	 * See also {@link ConfigSerializable}
	 */
	public abstract <T> ReferencedConfigBuilder<T> createReferencedConfig(PluginContainer container, T value);

	/**
	 * Creating a simple virtual configuration.
	 */
	public abstract VirtualConfigBuilder createVirtualConfig();

	/**
	 * Creating a reference virtual configuration that accepts and returns a serializable object of the specified type.<br>
	 * See also {@link ConfigSerializable}
	 */
	public abstract <T> ReferencedVirtualConfigBuilder<T> createVirtualReferencedConfig(Class<T> type, String rawData);

	/**
	 * Creating a reference virtual configuration that accepts and returns a serializable object of the specified type.<br>
	 * See also {@link ConfigSerializable}
	 */
	public abstract <T> ReferencedVirtualConfigBuilder<T> createVirtualReferencedConfig(Class<T> type, JsonObject rawData);

	/**
	 * Creating a reference virtual configuration that accepts and returns a serializable object of the specified type.<br>
	 * See also {@link ConfigSerializable}
	 */
	public abstract <T> ReferencedVirtualConfigBuilder<T> createVirtualReferencedConfig(T value);

	/**
	 * Creating a configuration loader with a type.
	 * The server administrator will not be able to override the configuration type created using this method.
	 * 
	 * @param <C>  - Configuration node processing class. Note that `{@link CommentedConfigurationNode}` is not suitable for configurations in Json format.
	 * @param path - Path to the configuration file.
	 * @param configType - Configuration Type. To avoid errors, it must point to the same class loader as the `Class<T> loaderClass` parameter.
	 * @return
	 */
	public abstract <C extends ConfigurationNode> ConfigurationLoader<C> createConfigLoader(Path path, ConfigTypes configType, @Nullable TypeSerializerCollection otherSerializers);

	/**
	 * Combining serializer collections into a single collection.
	 */
	public abstract TypeSerializerCollection mergeSerializers(@NotNull TypeSerializerCollection first, @Nullable TypeSerializerCollection second);

}
