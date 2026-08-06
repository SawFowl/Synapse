package sawfowl.synapse.implementapi.config;

import java.nio.file.Path;
import java.util.Objects;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import sawfowl.synapse.api.config.Config;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;

public class IReferencedConfig<T, N extends ConfigurationNode> extends IConfig implements ReferencedConfig<T> {

	public static final <T> IReferencedConfig<T, ConfigurationNode> create(Path configDir, String name, ConfigTypes configType, TypeSerializerCollection serializers, Class<T> clazz2) {
		return new IReferencedConfig<T, ConfigurationNode>(configDir, name, configType, serializers, clazz2);
	}

	public static final <T> IReferencedConfig<T, ConfigurationNode> create(Path configDir, String name, ConfigTypes configType, TypeSerializerCollection serializers, T object) {
		return new IReferencedConfig<T, ConfigurationNode>(configDir, name, configType, serializers, object);
	}

	private ConfigurationReference<N> configurationReference;
	private ValueReference<T, N> valueReference;
	private Class<T> clazz;

	protected IReferencedConfig(Path configDir, String name, ConfigTypes configType, TypeSerializerCollection serializers, Class<T> clazz) {
		super(configDir, name, configType, serializers);
		Objects.requireNonNull(clazz);
		this.clazz = clazz;
		load();
		if(!getPath().toFile().exists()) save();
	}

	@SuppressWarnings("unchecked")
	protected IReferencedConfig(Path configDir, String name, ConfigTypes configType, TypeSerializerCollection serializers, T object) {
		super(configDir, name, configType, serializers);
		Objects.requireNonNull(object);
		this.clazz = (Class<T>) object.getClass();
		load();
		if(!getPath().toFile().exists()) save(object);
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
	public <C extends Config> C load() {
		try {
			configurationReference = (ConfigurationReference<N>) super.selectLoader().loadToReference();
			configurationReference.load();
			valueReference = configurationReference.referenceTo(clazz);
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		return (C) this;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <C extends Config> C save() {
		valueReference.setAndSave(get());
		return (C) this;
	}

	@Override
	public <E extends T> void save(E object) {
		valueReference.setAndSave(object);
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
	public <T, O extends ReferencedConfig<T>> O toReference(T config) {
		return (O) this;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T, O extends ReferencedConfig<T>> O toReference(Class<T> config) {
		return (O) this;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T, O extends ReferencedConfig<T>> O toReference() {
		return (O) this;
	}

	@Override
	public boolean hasReferenced() {
		return true;
	}

}
