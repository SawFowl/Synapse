package sawfowl.synapse.api.config.builders;

import java.io.File;
import java.nio.file.Path;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;

public interface ReferencedConfigBuilder<T> {

	/**
	 * Loading the configuration from a file.<br>
	 * After using this method, you will not be able to change the file name and configuration type in this constructor.
	 */
	ReferencedConfigBuilder<T> fromFile(File file);

	/**
	 * @return The path to the configuration file.
	 */
	ReferencedConfigBuilder<T> setPath(Path configDir);

	/**
	 * The name of your configuration file. You don't need to specify the type here.
	 */
	ReferencedConfigBuilder<T> setName(String name);

	/**
	 * The type of the configuration file.
	 */
	ReferencedConfigBuilder<T> setType(ConfigTypes type);

	/**
	 * Additional serializers for your data.
	 */
	ReferencedConfigBuilder<T> addSerializers(TypeSerializerCollection collection);

	/**
	 * Creating configurations.
	 */
	ReferencedConfig<T>  build();

}
