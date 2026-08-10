package sawfowl.synapse.api.services;

import java.util.Locale;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.config.locale.Translation;

/**
 * The main API for working with localizations. Using this interface, you can register your collection of localizations for your plugin.
 */
public interface LocaleService {

	static LocaleService get() {
		return Synapse.getLocaleService();
	}

	/*
	 * Getting the system locale.<br>
	 * If game does not support your system locale, the default locale for game will be selected.
	 */
	public abstract Locale getSystemOrDefaultLocale();

	/**
	 * 
	 * Set the default serializable class object for all plugin localizations.<br>
	 * If no data has been previously written to this localization, it will be applied from the specified class.<br>
	 * This class will be applied automatically to all localizations loaded after its addition.<br>
	 * Automatic application of this class does not make any changes to the localization data.
	 * 
	 * @param container - {@link PluginContainer}
	 * @param defaultReference - The serializable class extends {@link Translation}
	 */
	public abstract <T extends Translation> void setDefaultReference(PluginContainer container, Class<T> defaultReference);

	/**
	 * Get the default serialization class for plugin localizations.<br>
	 * No type conversion is performed.
	 * 
	 * @param container - {@link PluginContainer}
	 * @return Serializable class, or null if no class assignment was previously made.
	 */
	public abstract <T extends Translation> Class<T> getDefaultReference(PluginContainer container);

	/**
	 * Same as {@linkplain #getDefaultReference(PluginContainer)}
	 */
	public abstract <T extends Translation> Class<T> getDefaultReference(String pluginID);

	/**
	 * Create and register localizations of your plugin.<br>
	 * Previously saved localizations will be uploaded automatically.<br>
	 * Localization files from your plugin's jar file will also be automatically saved and loaded.
	 * 
	 * @param container - Your plugin container.
	 */
	public abstract <T extends Translation> LocalesList<T> createLocales(PluginContainer container);

	/**
	 * 
	 * Create and register localizations of your plugin.<br>
	 * Previously saved localizations will be uploaded automatically.<br>
	 * Localization files from your plugin's jar file will also be automatically saved and loaded.
	 * 
	 * @param container - Your plugin container.
	 * @param translationReference  - Your serializable class for localizations. Do not specify interfaces and abstract classes here. Your class must be serializable.
	 * @return A collection of localizations of your plugin with the type you need. The type can be an interface or an abstract class.
	 */
	public abstract <T extends Translation> LocalesList<T> createLocales(PluginContainer container, Class<? extends T> translationReference);

	/**
	 * @return A collection of localizations of your plugin with the type you need. The type can be an interface or an abstract class.
	 */
	public abstract <T extends Translation> LocalesList<T> getLocales(PluginContainer container);

	/**
	 * @return A collection of localizations of your plugin with the type you need. The type can be an interface or an abstract class.
	 */
	public abstract <T extends Translation> LocalesList<T> getLocales(String plugin);

	/**
	 * Checking whether the collection of localizations for the specified plugin is registered.
	 */
	public abstract boolean localesExist(PluginContainer container);

	/**
	 * Checking whether the collection of localizations for the specified plugin is registered.
	 */
	public abstract boolean localesExist(String plugin);

}
