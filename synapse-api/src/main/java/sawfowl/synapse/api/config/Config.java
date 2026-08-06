package sawfowl.synapse.api.config;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import io.leangen.geantyref.TypeToken;

public interface Config {

	/**
	 * @return The path to the configuration file.
	 */
	Path getPath();

	/**
	 * The type of the configuration file.
	 */
	ConfigTypes getType();

	/**
	 * Getting the root section of the configuration.
	 */
	<N extends ConfigurationNode> N getRootNode();

	/**
	 * Getting the configuration loader.
	 */
	<N extends ConfigurationNode, L extends ConfigurationLoader<N>> L getLoader();

	/**
	 * Converting the configuration so that it is possible to work with a serializable class.
	 */
	<T, O extends ReferencedConfig<T>> O toReference(T config);

	/**
	 * Converting the configuration so that it is possible to work with a serializable class.
	 */
	<T, O extends ReferencedConfig<T>> O toReference(Class<T> config);

	/**
	 * Converting the configuration so that it is possible to work with a serializable class.<br>
	 * This method can return `null` if no other method has been used before with passing an object of the serializable class or specifying one.
	 */
	@Nullable <T, O extends ReferencedConfig<T>> O toReference();

	/**
	 * Adding an object to the current configuration if the specified configuration section does not exist.
	 */
	<T> boolean addIfNotExist(T object, @Nullable String comment, TypeToken<T> token, Object... path);

	/**
	 * Adding an object to the current configuration if the specified configuration section does not exist.
	 */
	<T> boolean addIfNotExist(T object, @Nullable String comment, Object... path);

	/**
	 * Adding an objects to the current configuration if the specified configuration section does not exist.
	 */
	<T> boolean addIfNotExist(List<T> object, @Nullable String comment, TypeToken<T> token, Object... path);

	/**
	 * Adding an objects to the current configuration if the specified configuration section does not exist.
	 */
	<T> boolean addIfNotExist(Class<T> clazz, List<T> object, @Nullable String comment, Object... path);

	/**
	 * Adding serializers to the current configuration.<br>
	 * The configuration file will be reloaded.
	 */
	void addSerializers(TypeSerializerCollection collection);

	/**
	 * Checking for the existence of a localization file on disk.
	 */
	boolean fileExist();

	/**
	 * Checking whether this configuration can be transformed in such a way that it is possible to work with a serializable class.
	 */
	boolean hasReferenced();

	/**
	 * This method can be used to reload the configuration file.
	 */
	<C extends Config> C load();

	/**
	 * Saving the configuration.
	 */
	<C extends Config> C save();

	/**
	 * Checking for the existence of a section in the configuration.
	 */
	default boolean contains(Object... path) {
		return !getRootNode().node(path).virtual();
	}

	/**
	 * See {@link ConfigurationNode#getString()}
	 */
	default String getString(Object... path) {
		return getRootNode().node(path).getString();
	}

	/**
	 * See {@link ConfigurationNode#getInt()}
	 */
	default int getInt(Object... path) {
		return getRootNode().node(path).getInt();
	}

	/**
	 * See {@link ConfigurationNode#getLong()}
	 */
	default long getLong(Object... path) {
		return getRootNode().node(path).getLong();
	}

	/**
	 * See {@link ConfigurationNode#getDouble()}
	 */
	default double getDouble(Object... path) {
		return getRootNode().node(path).getDouble();
	}

	/**
	 * See {@link ConfigurationNode#getFloat()}
	 */
	default float getFloat(Object... path) {
		return getRootNode().node(path).getFloat();
	}

	/**
	 * See {@link ConfigurationNode#getBoolean()}
	 */
	default boolean getBoolean(Object... path) {
		return getRootNode().node(path).getBoolean();
	}

	/**
	 * See {@link ConfigurationNode#getList(Class)}
	 */
	default <T> List<T> getList(Class<T> clazz, Object... path) {
		try {
			return getRootNode().node(path).getList(clazz);
		} catch (SerializationException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	/**
	 * See {@link ConfigurationNode#find(Class)}
	 */
	@Nullable default <T> T getObject(Class<T> clazz, Object... path) {
		try {
			return getRootNode().node(path).get(clazz);
		} catch (SerializationException e) {
			e.printStackTrace();
			return null;
		}
	}

}
