package sawfowl.synapse.api.config.builders;

import java.io.File;
import java.nio.file.Path;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import sawfowl.synapse.api.config.Config;
import sawfowl.synapse.api.config.ConfigTypes;

public interface ConfigBuilder {

	default ConfigBuilder fromFile(File file) {
		if(file.exists() && file.toPath().getParent() != null) {
			setPath(file.toPath().getParent());
			ConfigTypes type = ConfigTypes.find(ConfigTypes.getExtension(file.getName()));
			if(type != null) {
				setType(type);
				setName(file.getName().replace(type.toString(), ""));
			}
		}
		return this;
	}

	/**
	 * @return The path to the configuration file.
	 */
	ConfigBuilder setPath(Path configDir);

	/**
	 * The name of your configuration file. You don't need to specify the type here.
	 */
	ConfigBuilder setName(String name);

	/**
	 * The type of the configuration file.
	 */
	ConfigBuilder setType(ConfigTypes type);

	/**
	 * Additional serializers for your data.
	 */
	ConfigBuilder addSerializers(TypeSerializerCollection collection);

	/**
	 * Creating configurations.
	 */
	Config build();

}
