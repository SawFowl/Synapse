package sawfowl.synapse.implementapi.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Objects;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.jackson.FieldValueSeparatorStyle;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.xml.XmlConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.velocitypowered.api.plugin.PluginContainer;

import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import sawfowl.synapse.api.ResourceKey;
import sawfowl.synapse.api.commands.settings.CommandPrice;
import sawfowl.synapse.api.commands.settings.CommandSettings;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.LocalisedComment;
import sawfowl.synapse.api.config.builders.ConfigBuilder;
import sawfowl.synapse.api.config.builders.ReferencedConfigBuilder;
import sawfowl.synapse.api.config.builders.ReferencedVirtualConfigBuilder;
import sawfowl.synapse.api.config.builders.VirtualConfigBuilder;
import sawfowl.synapse.api.services.ConfigurationService;
import sawfowl.synapse.implementapi.LocalisedCommentFactory;
import sawfowl.synapse.implementapi.config.builders.IConfigBuilder;
import sawfowl.synapse.implementapi.config.builders.IReferencedBuilder;
import sawfowl.synapse.implementapi.config.builders.IReferencedVirtualBuilder;
import sawfowl.synapse.implementapi.config.builders.IVirtualConfigBuilder;
import sawfowl.synapse.implementapi.config.loaders.TomlConfigurationLoader;
import sawfowl.synapse.implementapi.config.serializers.SimpleDateFormatSerializer;
import sawfowl.synapse.implementapi.config.serializers.json.JsonArraySerializer;
import sawfowl.synapse.implementapi.config.serializers.json.JsonElementSerializer;
import sawfowl.synapse.implementapi.config.serializers.json.JsonObjectSerializer;
import sawfowl.synapse.implementapi.config.serializers.json.JsonPrimitiveSerializer;
import sawfowl.synapse.implementapi.config.serializers.synapse.BigDecimalSerializer;
import sawfowl.synapse.implementapi.config.serializers.synapse.CommandPriceSerializer;
import sawfowl.synapse.implementapi.config.serializers.synapse.CommandSettingsSerializer;
import sawfowl.synapse.implementapi.config.serializers.synapse.ConfigTypeSerializer;
import sawfowl.synapse.implementapi.config.serializers.synapse.ResourceKeySerializer;

public class IConfigurationService implements ConfigurationService {

	private static final IConfigurationService INSTANCE = new IConfigurationService();

	public static IConfigurationService getInstance() {
		return INSTANCE;
	}

	private IConfigurationService() {}

	private final ObjectMapper.Factory FACTORY = ObjectMapper.factoryBuilder().addProcessor(LocalisedComment.class, LocalisedCommentFactory.INSTANCE).addNodeResolver(NodeResolver.onlyWithSetting()).build();
	private final TypeSerializerCollection DEFAULT = TypeSerializerCollection.defaults()
			.childBuilder()
			.registerAnnotatedObjects(FACTORY)
			.register(SimpleDateFormat.class, SimpleDateFormatSerializer.INSTANCE)
			.register(ConfigTypes.class, ConfigTypeSerializer.INSTANCE)
			.register(JsonElement.class, JsonElementSerializer.INSTANCE)
			.register(JsonObject.class, JsonObjectSerializer.INSTANCE)
			.register(JsonArray.class, JsonArraySerializer.INSTANCE)
			.register(JsonPrimitive.class, JsonPrimitiveSerializer.INSTANCE)
			.registerAll(ConfigurateComponentSerializer.configurate().serializers())
			.register(ResourceKey.class, ResourceKeySerializer.INSTANCE)
			.register(CommandPrice.class, CommandPriceSerializer.INSTANCE)
			.register(CommandSettings.class, CommandSettingsSerializer.INSTANCE)
			.register(BigDecimal.class, BigDecimalSerializer.INSTANCE)
			.build();

	public TypeSerializerCollection getSerializers() {
		return DEFAULT;
	}

	private libs.synapse.geysermc.yaml.YamlConfigurationLoader.Builder createGeyserYamlConfigurationLoader(@Nullable TypeSerializerCollection otherSerializers) {
		return libs.synapse.geysermc.yaml.YamlConfigurationLoader.builder().defaultOptions(options -> options.serializers(otherSerializers == null ? DEFAULT : mergeSerializers(DEFAULT, otherSerializers))).nodeStyle(libs.synapse.geysermc.yaml.NodeStyle.BLOCK);
	}

	private YamlConfigurationLoader.Builder createYamlConfigurationLoader(@Nullable TypeSerializerCollection otherSerializers) {
		return YamlConfigurationLoader.builder().defaultOptions(options -> options.serializers(otherSerializers == null ? DEFAULT : mergeSerializers(DEFAULT, otherSerializers))).nodeStyle(NodeStyle.BLOCK);
	}

	private HoconConfigurationLoader.Builder createHoconConfigurationLoader(@Nullable TypeSerializerCollection otherSerializers) {
		return HoconConfigurationLoader.builder().defaultOptions(options -> options.serializers(otherSerializers == null ? DEFAULT : mergeSerializers(DEFAULT, otherSerializers)));
	}

	private GsonConfigurationLoader.Builder createJsonConfigurationLoader(@Nullable TypeSerializerCollection otherSerializers) {
		return GsonConfigurationLoader.builder().defaultOptions(options -> options.serializers(otherSerializers == null ? DEFAULT : mergeSerializers(DEFAULT, otherSerializers)));
	}

	private JacksonConfigurationLoader.Builder createJacksonConfigurationLoader(@Nullable TypeSerializerCollection otherSerializers) {
		return JacksonConfigurationLoader.builder().fieldValueSeparatorStyle(FieldValueSeparatorStyle.SPACE_BOTH_SIDES).defaultOptions(options -> options.serializers(otherSerializers == null ? DEFAULT : mergeSerializers(DEFAULT, otherSerializers)));
	}

	private XmlConfigurationLoader.Builder createXmlConfigurationLoader(@Nullable TypeSerializerCollection otherSerializers) {
		return XmlConfigurationLoader.builder().writesExplicitType(true).defaultOptions(options -> options.serializers(otherSerializers == null ? DEFAULT : mergeSerializers(DEFAULT, otherSerializers)));
	}

	private TomlConfigurationLoader.Builder createTomlConfigurationLoader(@Nullable TypeSerializerCollection otherSerializers) {
		return TomlConfigurationLoader.builder().defaultOptions(options -> options.serializers(otherSerializers == null ? DEFAULT : mergeSerializers(DEFAULT, otherSerializers)));
	}

	@Override
	public ConfigBuilder createSimpleConfig(PluginContainer container) {
		return new IConfigBuilder(container);
	}

	@Override
	public <T> ReferencedConfigBuilder<T> createReferencedConfig(PluginContainer container, Class<T> type) {
		return new IReferencedBuilder<>(container, type);
	}

	@Override
	public <T> ReferencedConfigBuilder<T> createReferencedConfig(PluginContainer container, T value) {
		return new IReferencedBuilder<>(container, value);
	}

	@Override
	public VirtualConfigBuilder createVirtualConfig() {
		return new IVirtualConfigBuilder();
	}

	@Override
	public <T> ReferencedVirtualConfigBuilder<T> createVirtualReferencedConfig(Class<T> type, String rawData) {
		return new IReferencedVirtualBuilder<>(type, rawData);
	}

	@Override
	public <T> ReferencedVirtualConfigBuilder<T> createVirtualReferencedConfig(Class<T> type, JsonObject rawData) {
		return new IReferencedVirtualBuilder<>(type, rawData);
	}

	@Override
	public <T> ReferencedVirtualConfigBuilder<T> createVirtualReferencedConfig(T value) {
		return new IReferencedVirtualBuilder<>(value);
	}

	@Override
	public TypeSerializerCollection mergeSerializers(@NotNull TypeSerializerCollection first, @Nullable TypeSerializerCollection second) {
		Objects.requireNonNull(first);
		return second == null ? first : first.childBuilder().registerAll(second).build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends ConfigurationNode> ConfigurationLoader<C> createConfigLoader(Path path, ConfigTypes configType, @Nullable TypeSerializerCollection otherSerializers) {
		switch (configType) {
			case HOCON: return (ConfigurationLoader<C>) createHoconConfigurationLoader(otherSerializers).path(path).build();
			case YAML: return (ConfigurationLoader<C>) createYamlConfigurationLoader(otherSerializers).path(path).build();
			case GEYSER_YAML: return (ConfigurationLoader<C>) createGeyserYamlConfigurationLoader(otherSerializers).path(path).build();
			case JSON: return (ConfigurationLoader<C>) createJsonConfigurationLoader(otherSerializers).path(path).build();
			case JACKSON: return (ConfigurationLoader<C>) createJacksonConfigurationLoader(otherSerializers).path(path).build();
			case XML: return (ConfigurationLoader<C>) createXmlConfigurationLoader(otherSerializers).path(path).build();
			case TOML: return (ConfigurationLoader<C>) createTomlConfigurationLoader(otherSerializers).path(path).build();
			default: throw new IllegalArgumentException("Inappropriate value: " + configType);
		}
	}

	@SuppressWarnings("unchecked")
	public <T, C extends ConfigurationNode> ConfigurationLoader<C> createConfigLoader(BufferedWriter bufferedWriter, BufferedReader bufferedReader, ConfigTypes loaderType, @Nullable TypeSerializerCollection otherSerializers) {
		switch (loaderType) {
			case HOCON: return (ConfigurationLoader<C>) createHoconConfigurationLoader(otherSerializers).sink(() -> bufferedWriter).source(() -> bufferedReader).build();
			case YAML: return (ConfigurationLoader<C>) createYamlConfigurationLoader(otherSerializers).sink(() -> bufferedWriter).source(() -> bufferedReader).build();
			case GEYSER_YAML: return (ConfigurationLoader<C>) createGeyserYamlConfigurationLoader(otherSerializers).sink(() -> bufferedWriter).source(() -> bufferedReader).build();
			case JSON: return (ConfigurationLoader<C>) createJsonConfigurationLoader(otherSerializers).sink(() -> bufferedWriter).source(() -> bufferedReader).build();
			case JACKSON: return (ConfigurationLoader<C>) createJacksonConfigurationLoader(otherSerializers).sink(() -> bufferedWriter).source(() -> bufferedReader).build();
			case XML: return (ConfigurationLoader<C>) createXmlConfigurationLoader(otherSerializers).sink(() -> bufferedWriter).source(() -> bufferedReader).build();
			case TOML: return (ConfigurationLoader<C>) createTomlConfigurationLoader(otherSerializers).sink(() -> bufferedWriter).source(() -> bufferedReader).build();
			default: throw new IllegalArgumentException("Inappropriate value: " + loaderType);
		}
	}

}
