package sawfowl.synapse.implementapi.config.builders;

import java.util.Objects;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import com.google.gson.JsonObject;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedVirtualConfig;
import sawfowl.synapse.api.config.builders.ReferencedVirtualConfigBuilder;
import sawfowl.synapse.implementapi.config.IReferencedVirtualConfig;

public class IReferencedVirtualBuilder<T> implements ReferencedVirtualConfigBuilder<T> {

	private final Class<T> clazz;
	private final T value;
	private ConfigTypes type;
	private TypeSerializerCollection collection;
	private String rawData;
	private JsonObject rawJsonData;
	public IReferencedVirtualBuilder(Class<T> type, String rawData) {
		Objects.requireNonNull(type);
		this.clazz = type;
		this.value = null;
		this.rawData = rawData;
	}

	public IReferencedVirtualBuilder(Class<T> type, JsonObject rawJsonData) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(rawJsonData);
		this.clazz = type;
		this.value = null;
		this.rawJsonData = rawJsonData;
	}

	@SuppressWarnings("unchecked")
	public IReferencedVirtualBuilder(T value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.clazz = (Class<T>) value.getClass();
	}

	@Override
	public ReferencedVirtualConfigBuilder<T> setType(ConfigTypes type) {
		if(type != null) this.type = type;
		return this;
	}

	@Override
	public ReferencedVirtualConfigBuilder<T> addSerializers(TypeSerializerCollection collection) {
		this.collection = collection;
		return this;
	}

	@Override
	public ReferencedVirtualConfig<T> build() {
		Objects.requireNonNull(type);
		Objects.requireNonNull(clazz);
		return value == null
			?
			rawJsonData == null 
				?
				IReferencedVirtualConfig.create(type, collection, clazz, rawData)
				:
				IReferencedVirtualConfig.create(type, collection, clazz, rawJsonData)
			:
			IReferencedVirtualConfig.create(type, collection, value);
	}

}
