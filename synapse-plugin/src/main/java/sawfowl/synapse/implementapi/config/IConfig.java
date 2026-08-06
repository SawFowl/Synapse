package sawfowl.synapse.implementapi.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import io.leangen.geantyref.TypeToken;

import sawfowl.synapse.api.config.Config;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;
import sawfowl.synapse.implementapi.services.IConfigurationService;

public class IConfig implements Config {

	public static final IConfig create(Path configDir, String name, ConfigTypes configType, TypeSerializerCollection serializers) {
		return new IConfig(configDir, name, configType, serializers);
	}

	private final ConfigTypes type;
	private ConfigurationNode node;
	private ConfigurationLoader<? extends ConfigurationNode> loader;
	private ReferencedConfig<?> referenced;
	private Path path;
	private String name;
	protected TypeSerializerCollection serializers;

	protected IConfig(Path configDir, String name, ConfigTypes configType, TypeSerializerCollection serializers) {
		this.path = configDir.resolve(name + configType.toString());
		this.type = configType;
		this.name = name;
		this.serializers = serializers;
		if(!(this instanceof IReferencedConfig)) load();
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public ConfigTypes getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <N extends ConfigurationNode> N getRootNode() {
		return (N) node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <N extends ConfigurationNode, L extends ConfigurationLoader<N>> L getLoader() {
		return (L) loader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, O extends ReferencedConfig<T>> O toReference(T config) {
		Objects.requireNonNull(config);
		return (O) (referenced == null ? (referenced = IReferencedConfig.create(path, getName(), type, serializers, config)) : referenced);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, O extends ReferencedConfig<T>> O toReference(Class<T> config) {
		Objects.requireNonNull(config);
		return (O) (referenced == null ? (referenced = IReferencedConfig.create(path, getName(), type, serializers, config)) : referenced);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, O extends ReferencedConfig<T>> O toReference() {
		return (O) referenced;
	}

	@Override
	public <T> boolean addIfNotExist(T object, @Nullable String comment, TypeToken<T> token, Object... path) {
		Objects.requireNonNull(object);
		Objects.requireNonNull(token);
		Objects.requireNonNull(path);
		if(getRootNode().node(path).virtual()) {
			try {
				getRootNode().node(path).set(token, object);
				if(comment != null && getRootNode() instanceof CommentedConfigurationNode commented) commented.node(path).comment(comment);
				return true;
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public <T> boolean addIfNotExist(T object, @Nullable String comment, Object... path) {
		Objects.requireNonNull(object);
		Objects.requireNonNull(path);
		if(getRootNode().node(path).virtual()) {
			try {
				getRootNode().node(path).set(object.getClass(), object);
				if(comment != null && getRootNode() instanceof CommentedConfigurationNode commented) commented.node(path).comment(comment);
				return true;
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public <T> boolean addIfNotExist(List<T> object, @Nullable String comment, TypeToken<T> token, Object... path) {
		Objects.requireNonNull(object);
		Objects.requireNonNull(token);
		Objects.requireNonNull(path);
		if(getRootNode().node(path).virtual()) {
			try {
				getRootNode().node(path).setList(token, object);
				if(comment != null && getRootNode() instanceof CommentedConfigurationNode commented) commented.node(path).comment(comment);
				return true;
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public <T> boolean addIfNotExist(Class<T> clazz, List<T> object, @Nullable String comment, Object... path) {
		Objects.requireNonNull(object);
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(path);
		if(getRootNode().node(path).virtual()) {
			try {
				getRootNode().node(path).setList(clazz, object);
				if(comment != null && getRootNode() instanceof CommentedConfigurationNode commented) commented.node(path).comment(comment);
				return true;
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void addSerializers(TypeSerializerCollection collection) {
		if(serializers == null) {
			serializers = collection;
		} else serializers = serializers.childBuilder().registerAll(collection).build();
		try {
			loader = selectLoader();
			node = loader.load();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean fileExist() {
		return path.toFile().exists();
	}

	@Override
	public boolean hasReferenced() {
		return referenced != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Config> C load() {
		try {
			if(loader == null) loader = selectLoader();
			node = loader.load();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Config> C save() {
		try {
			loader.save(node);
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		return (C) this;
	}

	<T, C extends ConfigurationNode> ConfigurationLoader<C> selectLoader() {
		return IConfigurationService.getInstance().createConfigLoader(path, type, serializers);
	}

	protected String getName() {
		return name;
	}

	public TypeSerializerCollection getSerializers() {
		return serializers;
	}

}
