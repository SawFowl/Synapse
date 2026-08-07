package sawfowl.synapse.implementapi.config.loaders.converters;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;

/**
 * Extracts and restores comments from ConfigurationNode
 * For TOML comments, use the loader's built-in methods
 */
public final class CommentProcessor {

	private CommentProcessor() {}

	/**
	 * Extracts comments from ConfigurationNode
	 */
	public static Map<String, String> extractCommentsFromNode(ConfigurationNode node, String path) {
		Map<String, String> result = new LinkedHashMap<>();
		if(node.isMap()) {
			for(Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
				String key = entry.getKey().toString();
				String fullPath = path.isEmpty() ? key : path + "." + key;
				ConfigurationNode child = entry.getValue();
				if(child instanceof CommentedConfigurationNode commented) {
					String comment = commented.comment();
					if(comment != null && !comment.isEmpty()) {
						result.put(fullPath, comment);
					}
				}
				if(child.isMap()) {
					result.putAll(extractCommentsFromNode(child, fullPath));
				}
			}
		}
		return result;
	}

	/**
	 * Restores comments to ConfigurationNode
	 */
	public static void restoreCommentsToNode(ConfigurationNode node, Map<String, String> comments, String currentPath) {
		if(node.isMap()) {
			for(Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
				String key = entry.getKey().toString();
				String fullPath = currentPath.isEmpty() ? key : currentPath + "." + key;
				ConfigurationNode child = entry.getValue();
				String comment = comments.get(fullPath);
				if(comment != null && child instanceof CommentedConfigurationNode commented) {
					if(comment.contains("\n")) {
						comment = String.join("\n", Arrays.stream(comment.split("\n"))
							.map(String::trim)
							.toArray(String[]::new));
					} else {
						comment = comment.trim();
					}
					commented.comment(comment);
				}
				if(child.isMap()) {
					restoreCommentsToNode(child, comments, fullPath);
				}
			}
		}
	}

}