package sawfowl.synapse.configure.localization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class ConfigComments {

	public static ConfigComments createRu() {
		ConfigComments comments = new ConfigComments();
		comments.configType = "Тип конфигурации по умолчанию, если ее тип не был указан плагином.\n"
				+ "Это всегда применяется к основной конфигурации SYNAPSE.\n"
				+ "Это никогда не применяется к виртуальным конфигурациям. Их тип всегда должен быть указан разработчиком плагина, который их использует.\n"
				+ "Допустимые варианты:\n"
				+ "Hocon - Стандартная конфигурация Sponge. Имеет некоторое сходство с Json. Поддерживает комментарии.\n"
				+ "Json - Альтернативный вид конфигурации Sponge. Классический Json. Не поддерживает комментарии.\n"
				+ "Yaml - Альтернативный вид конфигурации Sponge. Использует блочную структуру. Наиболее удобен для чтения человеку. На текущий момент не поддерживает комментарии, возможно будет исправленно в будущем командой Sponge.\n"
				+ "GeyserYaml - Альтернативный вид конфигурации Sponge. Использует блочную структуру. Наиболее удобен для чтения человеку. Поддерживает комментарии. Поддержка данного загрузчика является экспериментальной.\n"
				+ "Jackson - Альтернативный вид конфигурации в формате json Sponge. Не поддерживает комментарии.\n"
				+ "XML - Альтернативный вид конфигурации Sponge. Поддерживает комментарии, но не удобен для чтения.\n"
				+ "Toml - Использование созданного в SYNAPSE загрузчика конфигурации формата Toml. Удобен для чтения. Поддерживает комментарии. Не рекомендуется для локализаций, так как при последующей конвертации из него в любой другой формат могут возникнуть ошибки из-за несовместимости Toml с форматом данных json.";
		comments.forcedUse = "Принудительное применение указанного тут типа конфигурации к другим плагинам, которые используют возможности SYNAPSE.";
		comments.path = "Путь по умолчанию к локализациям.\n"
				+ "{SYNAPSE_PATH} - Указывает на каталог конфигурации плагина SYNAPSE.\n"
				+ "{PLUGIN_CONFIG_PATH} - Указывает на каталог плагина, который регистрирует локализации.\n"
				+ "{PATH_SEPARATOR} - Символ разделяющий каталоги.\n"
				+ "Пример: {SYNAPSE_PATH}{PATH_SEPARATOR}locales{PATH_SEPARATOR}";
		return comments;
	}

	public ConfigComments(){}

	@Setting("ConfigType")
	private String configType = "The default configuration type if the type was not specified by the plugin.\n"
			+ "This always applies to the main SYNAPSE configuration.\n"
			+ "This never applies to virtual configurations, which must always be specified by the plugin developer that uses them.\n"
			+ "Acceptable variants:\n"
			+ "Hocon - Standard Sponge configuration. Has some similarities with Json. Supports comments.\n"
			+ "Json - Alternative configuration format. Classic Json. Does not support comments.\n"
			+ "Yaml - An alternative Sponge configuration format that uses a block structure and is easy to read. Currently, it does not support comments, but this may be fixed in the future by the Sponge team.\n"
			+ "GeyserYaml - An alternative Sponge configuration format that uses a block structure and is easy to read. It supports comments. Support for this loader is experimental.\n"
			+ "Jackson - An alternative Sponge configuration format in json format that does not support comments.\n"
			+ "XML - An alternative Sponge configuration view that supports comments but is not easy to read.\n"
			+ "Toml - Using the SYNAPSE-generated Toml format configuration loader. Easy to read. Supports comments. It is not recommended for localizations, as errors may occur during subsequent conversion from it to any other format due to the incompatibility of Toml with the json data format.";
	@Setting("ForcedUse")
	private String forcedUse = "Forces the specified configuration type to be applied to other plugins that use the SYNAPSE.";
	@Setting("Path")
	private String path = "The default path to localizations.\n"
			+ "{SYNAPSE_PATH} - Specifies the configuration directory for the SYNAPSE plugin.\n"
			+ "{PLUGIN_CONFIG_PATH} - Specifies the plugin directory that registers localizations.\n"
			+ "{PATH_SEPARATOR} - Directory separator symbol.\n"
			+ "Example: {SYNAPSE_PATH}{PATH_SEPARATOR}locales{PATH_SEPARATOR}";

}
