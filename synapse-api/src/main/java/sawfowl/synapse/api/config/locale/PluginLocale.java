package sawfowl.synapse.api.config.locale;

import java.util.List;
import java.util.Locale;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.config.Config;
import sawfowl.synapse.api.text.Text;

public interface PluginLocale extends Config {

	/**
	 * See {@link Locale}
	 */
	Locale getLocale();

	/**
	 * Converting the configuration so that it is possible to work with a serializable class.<br>
	 * When working with localization, it is recommended to use this method to get a configuration with a serializable class.
	 */
	<L extends Translation, O extends ReferencedLocale<L>> O toReferenceTranslation(L config);

	/**
	 * Converting the configuration so that it is possible to work with a serializable class.<br>
	 * When working with localization, it is recommended to use this method to get a configuration with a serializable class.
	 */
	<L extends Translation, O extends ReferencedLocale<L>> O toReferenceTranslation(Class<L> config);

	/**
	 * Getting a deserialized list of {@link Component} classes from the locale configuration node. 
	 * 
	 * @param path- Path in the config file.
	 */
	default List<Component> getComponents(Object... path) {
		return getList(Component.class, path);
	}

	/**
	 * Getting formatted text from the current localization configuration.
	 * 
	 * @param path - Path in the config file.
	 */
	default Component getComponent(Object... path) {
		return getObject(Component.class, path);
	}

	/**
	 * Getting deserialized text in the constructor for its further modification.<br>
	 * The operation is possible only after the constructor is registered in the {@link RegisterBuilderEvent} event.
	 */
	default Text getText(Object... path) {
		return Text.of(getComponent(path));
	}

	/**
	 * Getting deserialized text in the constructor for its further modification.<br>
	 * The operation is possible only after the constructor is registered in the {@link RegisterBuilderEvent} event.
	 */
	default List<Text> getTexts(Object... path) {
		return getComponents(path).stream().map(Text::of).toList();
	}

}
