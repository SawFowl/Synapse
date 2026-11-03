package sawfowl.synapse.implementapi.config;

import java.util.Objects;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedVirtualConfig;
import sawfowl.synapse.api.config.VirtualConfig;

public class IReferencedVirtualConfig<T, N extends ConfigurationNode> extends IVirtualConfig implements ReferencedVirtualConfig<T> {

	public static final <T> IReferencedVirtualConfig<T, ConfigurationNode> create(ConfigTypes configType, TypeSerializerCollection serializers, Class<T> clazz, String rawData) {
		return new IReferencedVirtualConfig<T, ConfigurationNode>(configType, serializers, clazz, rawData);
	}

	public static final <T> IReferencedVirtualConfig<T, ConfigurationNode> create(ConfigTypes configType, TypeSerializerCollection serializers, Class<T> clazz, JsonObject rawJsonData) {
		return new IReferencedVirtualConfig<T, ConfigurationNode>(configType, serializers, clazz, rawJsonData);
	}

	public static final <T> IReferencedVirtualConfig<T, ConfigurationNode> create(ConfigTypes configType, TypeSerializerCollection serializers, T object) {
		return new IReferencedVirtualConfig<T, ConfigurationNode>(configType, serializers, object, "");
	}

	/**
	 * Loads the raw data; the object is used only if the raw data is empty.
	 */
	public static final <T> IReferencedVirtualConfig<T, ConfigurationNode> createWithDefault(ConfigTypes configType, TypeSerializerCollection serializers, T object, String rawData) {
		return new IReferencedVirtualConfig<T, ConfigurationNode>(configType, serializers, object, rawData);
	}

	private ConfigurationReference<N> configurationReference;
	private ValueReference<T, N> valueReference;
	private Class<T> clazz;
	private JsonObject rawJsonData;
	protected IReferencedVirtualConfig(ConfigTypes configType, TypeSerializerCollection serializers, Class<T> clazz, String rawData) {
		super(rawData, configType, serializers);
		Objects.requireNonNull(clazz);
		this.clazz = clazz;
		load();
		save();
	}

	protected IReferencedVirtualConfig(ConfigTypes configType, TypeSerializerCollection serializers, Class<T> clazz, JsonObject rawJsonData) {
		super("", configType, serializers);
		Objects.requireNonNull(clazz);
		this.clazz = clazz;
		// The field was never assigned, so the JSON data was ignored and the defaults were saved.
		this.rawJsonData = rawJsonData;
		loadFromJson();
		save();
	}

	@SuppressWarnings("unchecked")
	protected IReferencedVirtualConfig(ConfigTypes configType, TypeSerializerCollection serializers, T object, String rawData) {
		super(rawData == null ? "" : rawData, configType, serializers);
		Objects.requireNonNull(object);
		this.clazz = (Class<T>) object.getClass();
		load();
		if(getRawData().isBlank()) {
			save(object);
		} else save();
	}

	@Override
	public ReferencedVirtualConfig<T> loadFromRaw(String rawData) {
		Objects.requireNonNull(rawData);
		super.rawData = rawData;
		load();
		return this;
	}

	@Override
	public T convertFromJson(JsonElement element) {
		try {
			valueReference.node().set(element);
			configurationReference.save(valueReference.node());
			// The raw data first: load() reads it, and it used to read the old data.
			updateRawData();
			load();
			return valueReference.get();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <E extends JsonElement> E toJson(Class<E> jsonClass) {
		try {
			return valueReference.node().get(jsonClass);
		} catch (SerializationException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ConfigurationReference<N> getReference() {
		return configurationReference;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ValueReference<T, N> getValueReference() {
		return valueReference;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <N extends ConfigurationNode, L extends ConfigurationLoader<N>> L getLoader() {
		return (L) getReference().loader();
	}

	@SuppressWarnings("unchecked")
	@Override
	public N getRootNode() {
		return getValueReference().node();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <C extends VirtualConfig> C load() {
		try {
			configurationReference = (ConfigurationReference<N>) super.selectLoader().loadToReference();
			// configurationReference.load();
			valueReference = configurationReference.referenceTo(clazz);
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		return (C) this;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <C extends VirtualConfig> C save() {
		valueReference.setAndSave(get());
		updateRawData();
		load();
		return (C) this;
	}

	@Override
	public <E extends T> void save(E object) {
		valueReference.setAndSave(object);
		updateRawData();
		load();
	}

	@Override
	public void addSerializers(TypeSerializerCollection collection) {
		if(serializers == null) {
			serializers = collection;
		} else serializers = serializers.childBuilder().registerAll(collection).build();
		load();
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T, O extends ReferencedVirtualConfig<T>> O toReference(T config) {
		return (O) this;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T, O extends ReferencedVirtualConfig<T>> O toReference(Class<T> config) {
		return (O) this;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T, O extends ReferencedVirtualConfig<T>> O toReference() {
		return (O) this;
	}

	@Override
	public boolean hasReferenced() {
		return true;
	}

	@SuppressWarnings("unchecked")
	private void loadFromJson() {
		try {
			configurationReference = (ConfigurationReference<N>) super.selectLoader().loadToReference();
			configurationReference.set(NodePath.path(), rawJsonData);
			valueReference = configurationReference.referenceTo(clazz);
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
	}

}
