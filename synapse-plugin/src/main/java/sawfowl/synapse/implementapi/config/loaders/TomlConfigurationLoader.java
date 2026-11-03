package sawfowl.synapse.implementapi.config.loaders;

import libs.synapse.com.electronwill.nightconfig.core.CommentedConfig;
import libs.synapse.com.electronwill.nightconfig.core.Config;
import libs.synapse.com.electronwill.nightconfig.toml.TomlFormat;
import libs.synapse.com.electronwill.nightconfig.toml.TomlWriter;
import sawfowl.synapse.implementapi.config.loaders.converters.AdventurePrimitiveConverter;
import sawfowl.synapse.implementapi.config.loaders.converters.CommentProcessor;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.loader.CommentHandler;
import org.spongepowered.configurate.loader.CommentHandlers;
import org.spongepowered.configurate.loader.ParsingException;

import java.io.*;
import java.util.*;
import java.time.temporal.Temporal;
import java.util.stream.Stream;

public class TomlConfigurationLoader extends AbstractConfigurationLoader<CommentedConfigurationNode> {

	public static Builder builder() {
		return new Builder();
	}

	private TomlConfigurationLoader(Builder builder) {
		super(builder, new CommentHandler[]{CommentHandlers.HASH});
	}

	@Override
	protected void loadInternal(CommentedConfigurationNode node, BufferedReader reader) throws ParsingException {
		try {
			// Straight from TOML to plain maps: the old way went TOML -> JSON text -> config -> maps,
			// and turned nested arrays into strings on the way.
			CommentedConfig tomlConfig = TomlFormat.instance().createParser().parse(reader);
			Map<String, String> comments = extractComments(tomlConfig, "");
			node.raw(convertToPlainMap(tomlConfig));
			CommentProcessor.restoreCommentsToNode(node, comments, "");
		} catch (Exception e) {
			throw new ParsingException(node, 0, 0, null, "Error parsing TOML", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void saveInternal(ConfigurationNode node, Writer writer) throws ConfigurateException {
		try {
			// Get data from node
			Object raw = node.raw();

			// Handle null value - skip saving
			if(raw == null) {
				return;
			}

			// Convert to Map if needed, or handle other types
			Map<String, Object> dataMap;
			if(raw instanceof Map) {
				dataMap = (Map<String, Object>) raw;
			} else {
				// Wrap non-Map values in a default root structure
				dataMap = new LinkedHashMap<>();
				dataMap.put("value", raw);
			}

			// Extract comments from the node
			Map<String, String> comments = CommentProcessor.extractCommentsFromNode(node, "");

			// Convert all objects to primitives before saving
			Map<String, Object> convertedMap = AdventurePrimitiveConverter.convertMapValuesToPrimitive(dataMap);

			// Convert Map to TOML Config
			CommentedConfig nightConfig = convertMapToToml(convertedMap);

			// Restore comments to TOML Config
			restoreCommentsToConfig(nightConfig, comments, "");

			// Save to TOML
			TomlWriter tomlWriter = new TomlWriter();
			StringWriter stringWriter = new StringWriter();
			tomlWriter.write(nightConfig, stringWriter);
			writer.write(stringWriter.toString());
			writer.flush();
		} catch (Exception e) {
			throw new ConfigurateException(e);
		}
	}

	@Override
	public CommentedConfigurationNode createNode(ConfigurationOptions options) {
		return CommentedConfigurationNode.root(options);
	}

	/**
	 * Extracts comments from TOML Config
	 */
	private Map<String, String> extractComments(CommentedConfig config, String path) {
		Map<String, String> result = new LinkedHashMap<>();
		for(Config.Entry entry : config.entrySet()) {
			String key = entry.getKey();
			String fullPath = path.isEmpty() ? key : path + "." + key;
			String comment = config.getComment(Collections.singletonList(key));
			if(comment != null && !comment.isEmpty()) {
				result.put(fullPath, comment);
			}
			Object value = entry.getValue();
			if(value instanceof Config subConfig) {
				result.putAll(extractComments((CommentedConfig) subConfig, fullPath));
			}
		}
		return result;
	}

	/**
	 * Restores comments to TOML Config
	 */
	private void restoreCommentsToConfig(CommentedConfig config, Map<String, String> comments, String currentPath) {
		for(Config.Entry entry : config.entrySet()) {
			String key = entry.getKey();
			String fullPath = currentPath.isEmpty() ? key : currentPath + "." + key;
			String comment = comments.get(fullPath);
			if(comment != null) {
				// Format comment for TOML
				if(comment.contains("\n")) {
					comment = String.join("\n", Stream.of(comment.split("\n"))
						.map(line -> line.startsWith(" ") ? line : " " + line)
						.toArray(String[]::new));
				} else if(!comment.startsWith(" ")) {
					comment = " " + comment;
				}
				config.setComment(Collections.singletonList(key), comment);
			}
			Object value = entry.getValue();
			if(value instanceof CommentedConfig subConfig) {
				restoreCommentsToConfig(subConfig, comments, fullPath);
			}
		}
	}

	/**
	 * Recursively converts Config to plain Map
	 */
	private Map<String, Object> convertToPlainMap(Config config) {
		Map<String, Object> result = new LinkedHashMap<>();
		for(Config.Entry entry : config.entrySet()) result.put(entry.getKey(), toPlain(entry.getValue()));
		return result;
	}

	private Object toPlain(Object value) {
		if(value instanceof Config subConfig) return convertToPlainMap(subConfig);
		if(value instanceof List<?> list) {
			List<Object> result = new ArrayList<>(list.size());
			for(Object item : list) result.add(toPlain(item));
			return result;
		}
		// TOML dates and times; the JSON round trip used to turn them into strings as well.
		if(value instanceof Temporal) return value.toString();
		return value;
	}

	/**
	 * Converts Map to TOML Config.<br>
	 * Keys are set as one path element: {@code set(String)} splits on dots,
	 * so a key such as {@code "127.0.0.1"} turned into nested tables.
	 */
	private CommentedConfig convertMapToToml(Map<String, Object> map) {
		CommentedConfig result = TomlFormat.instance().createConfig();
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			Object value = toToml(entry.getValue());
			// TOML has no null.
			if(value != null) result.set(Collections.singletonList(entry.getKey()), value);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Object toToml(Object value) {
		if(value instanceof Map<?, ?> map) return convertMapToToml((Map<String, Object>) map);
		if(value instanceof List<?> list) {
			List<Object> result = new ArrayList<>(list.size());
			for(Object item : list) {
				Object converted = toToml(item);
				if(converted != null) result.add(converted);
			}
			return result;
		}
		return value;
	}

	public static final class Builder extends AbstractConfigurationLoader.Builder<Builder, TomlConfigurationLoader> {

		private Builder() {}

		@Override
		public TomlConfigurationLoader build() {
			return new TomlConfigurationLoader(this);
		}
	}

}