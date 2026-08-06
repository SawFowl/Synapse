package sawfowl.synapse.implementapi.config.builders;

import java.util.Objects;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.VirtualConfig;
import sawfowl.synapse.api.config.builders.VirtualConfigBuilder;
import sawfowl.synapse.implementapi.config.IVirtualConfig;

public class IVirtualConfigBuilder implements VirtualConfigBuilder {

	private String rawData = "";
	private ConfigTypes type;
	private TypeSerializerCollection collection;
	public IVirtualConfigBuilder() {
		if(SynapsePlugin.getConfig() != null) this.type = SynapsePlugin.getConfig().getConfigSettings().getType();
	}

	@Override
	public VirtualConfigBuilder setData(String rawData) {
		Objects.requireNonNull(rawData);
		this.rawData = rawData;
		return this;
	}

	@Override
	public VirtualConfigBuilder setType(ConfigTypes type) {
		Objects.requireNonNull(type);
		this.type = type;
		return this;
	}

	@Override
	public VirtualConfigBuilder addSerializers(TypeSerializerCollection collection) {
		this.collection = collection;
		return this;
	}

	@Override
	public VirtualConfig build() {
		Objects.requireNonNull(type);
		return IVirtualConfig.create(rawData, type, collection);
	}

}
