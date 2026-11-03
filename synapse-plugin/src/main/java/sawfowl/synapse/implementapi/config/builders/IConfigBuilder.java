package sawfowl.synapse.implementapi.config.builders;

import java.nio.file.Path;
import java.util.Objects;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.config.Config;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.builders.ConfigBuilder;
import sawfowl.synapse.implementapi.config.FormatMigration;
import sawfowl.synapse.implementapi.config.IConfig;

public class IConfigBuilder implements ConfigBuilder {

	private Path configDir;
	private String name;
	private ConfigTypes type;
	private TypeSerializerCollection collection;
	private PluginContainer container;
	public IConfigBuilder(PluginContainer container) {
		if(SynapsePlugin.getConfig() != null) this.type = SynapsePlugin.getConfig().getConfigSettings(container).getType();
		this.container = container;
	}

	@Override
	public ConfigBuilder setPath(Path configDir) {
		this.configDir = configDir;
		return this;
	}

	@Override
	public ConfigBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public ConfigBuilder setType(ConfigTypes type) {
		if(type != null) this.type = type;
		return this;
	}

	@Override
	public ConfigBuilder addSerializers(TypeSerializerCollection collection) {
		this.collection = collection;
		return this;
	}

	@Override
	public Config build() {
		Objects.requireNonNull(configDir);
		Objects.requireNonNull(name);
		if(SynapsePlugin.getConfig() != null && SynapsePlugin.getConfig().getConfigSettings(container).isForcedUse()) {
			return FormatMigration.open(configDir, name, SynapsePlugin.getConfig().getConfigSettings(container).getType(), type -> IConfig.create(configDir, name, type, collection));
		}
		Objects.requireNonNull(type);
		return IConfig.create(configDir, name, type, collection);
	}

}
