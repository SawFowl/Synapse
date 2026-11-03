package sawfowl.synapse.implementapi.config;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

/**
 * The shared body of the {@code addIfNotExist} methods of file and virtual configs.
 */
final class NodeDefaults {

	@FunctionalInterface
	interface Setter {
		void set(ConfigurationNode node) throws SerializationException;
	}

	private NodeDefaults() {}

	static boolean addIfNotExist(ConfigurationNode root, @Nullable String comment, Object[] path, Setter setter) {
		Objects.requireNonNull(path);
		ConfigurationNode node = root.node(path);
		if(!node.virtual()) return false;
		try {
			setter.set(node);
			if(comment != null && node instanceof CommentedConfigurationNode commented) commented.comment(comment);
			return true;
		} catch (SerializationException e) {
			e.printStackTrace();
			return false;
		}
	}

}
