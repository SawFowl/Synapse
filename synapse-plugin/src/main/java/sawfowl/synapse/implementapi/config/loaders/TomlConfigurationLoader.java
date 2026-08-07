package sawfowl.synapse.implementapi.config.loaders;

import libs.synapse.com.electronwill.nightconfig.core.CommentedConfig;
import libs.synapse.com.electronwill.nightconfig.core.Config;
import libs.synapse.com.electronwill.nightconfig.core.io.ParsingMode;
import libs.synapse.com.electronwill.nightconfig.json.JsonParser;
import libs.synapse.com.electronwill.nightconfig.toml.TomlFormat;
import libs.synapse.com.electronwill.nightconfig.toml.TomlWriter;
import sawfowl.synapse.implementapi.config.loaders.converters.AdventurePrimitiveConverter;
import sawfowl.synapse.implementapi.config.loaders.converters.CommentProcessor;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
			// Read entire TOML file into a string
			StringBuilder content = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}

			// Load TOML with comments
			CommentedConfig tomlConfig = TomlFormat.instance().createConfig();
			tomlConfig = TomlFormat.instance().createParser().parse(content.toString());

			// Extract comments separately
			Map<String, String> comments = extractComments(tomlConfig, "");

			// Convert TOML to JSON string
			String jsonString = convertTomlToJson(tomlConfig);

			// Parse JSON into Config
			JsonParser jsonParser = new JsonParser();
			CommentedConfig nightConfig = TomlFormat.instance().createConfig();
			jsonParser.parse(jsonString, nightConfig, ParsingMode.REPLACE);

			// Convert to Map and set into node
			Map<String, Object> dataMap = convertToPlainMap(nightConfig);
			node.raw(dataMap);

			// Restore comments in the node
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
			String comment = config.getComment(key);
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
				config.setComment(key, comment);
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
		for(Config.Entry entry : config.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof Config subConfig) {
				result.put(key, convertToPlainMap(subConfig));
			} else if(value instanceof List<?> list) {
				List<Object> convertedList = new ArrayList<>();
				for(Object item : list) {
					if(item instanceof Config itemConfig) {
						convertedList.add(convertToPlainMap(itemConfig));
					} else {
						convertedList.add(item);
					}
				}
				result.put(key, convertedList);
			} else {
				result.put(key, value);
			}
		}
		return result;
	}

	/**
	 * Converts TOML Config to JSON string
	 */
	private String convertTomlToJson(CommentedConfig tomlConfig) {
		JsonObject jsonObject = new JsonObject();
		copyConfigToJson(tomlConfig, jsonObject);
		return jsonObject.toString();
	}

	/**
	 * Recursively copies data from TOML Config to JsonObject
	 */
	private void copyConfigToJson(Config source, JsonObject target) {
		for(Config.Entry entry : source.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof Config subConfig) {
				JsonObject subJson = new JsonObject();
				copyConfigToJson(subConfig, subJson);
				target.add(key, subJson);
			} else if(value instanceof List<?> list) {
				com.google.gson.JsonArray jsonArray = new com.google.gson.JsonArray();
				for(Object item : list) {
					if(item instanceof Config itemConfig) {
						JsonObject subJson = new JsonObject();
						copyConfigToJson(itemConfig, subJson);
						jsonArray.add(subJson);
					} else if(item instanceof String) {
						jsonArray.add(new JsonPrimitive((String) item));
					} else if(item instanceof Number) {
						jsonArray.add(new JsonPrimitive((Number) item));
					} else if(item instanceof Boolean) {
						jsonArray.add(new JsonPrimitive((Boolean) item));
					} else if(item != null) {
						jsonArray.add(new JsonPrimitive(item.toString()));
					}
				}
				target.add(key, jsonArray);
			} else if(value instanceof String) {
				target.addProperty(key, (String) value);
			} else if(value instanceof Number) {
				target.addProperty(key, (Number) value);
			} else if(value instanceof Boolean) {
				target.addProperty(key, (Boolean) value);
			} else if(value != null) {
				target.addProperty(key, value.toString());
			}
		}
	}

	/**
	 * Converts Map to TOML Config
	 */
	@SuppressWarnings("unchecked")
	private CommentedConfig convertMapToToml(Map<String, Object> map) {
		CommentedConfig result = TomlFormat.instance().createConfig();
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof Map) {
				result.set(key, convertMapToToml((Map<String, Object>) value));
			} else if(value instanceof List<?> list) {
				List<Object> convertedList = new ArrayList<>();
				for(Object item : list) {
					if(item instanceof Map) {
						convertedList.add(convertMapToToml((Map<String, Object>) item));
					} else {
						convertedList.add(item);
					}
				}
				result.set(key, convertedList);
			} else {
				result.set(key, value);
			}
		}
		return result;
	}

	public static final class Builder extends AbstractConfigurationLoader.Builder<Builder, TomlConfigurationLoader> {

		private Builder() {}

		@Override
		public TomlConfigurationLoader build() {
			return new TomlConfigurationLoader(this);
		}
	}

}