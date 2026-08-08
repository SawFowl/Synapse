package sawfowl.synapse.api.config.locale;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.services.LocaleService;

/**
 * A collection of localizations for your plugin.<br>
 * You can use both simple localization configurations and reference localizations.<br>
 * When using reference localizations, make sure that they all have the same type or implement one interface that they share.<br>
 * When using reference localizations, you can also cast this list to the list of localizations in the method in your plugin, indicating your type.
 * @param <T> - Your class or interface that inherits the `Translation` interface. It will be `null` if you did not specify the type when creating this list.
 */
public interface LocalesList<@Nullable T extends Translation> {

	/**
	 * Creating a simple localization. You will need to write and read data from the configuration yourself.<br>
	 * See {@link ConfigurationNode} for more info.
	 */
	PluginLocale createSimpleTranslation(ConfigTypes configType, Locale locale);

	/**
	 * Creating a reference localization. You will not need to write and read data yourself by accessing the configuration sections.<br>
	 * You will be provided with the object you specified with all the data from the configuration file corresponding to the specified localization.
	 */
	<O extends T> ReferencedLocale<O> createReferencedTranslation(ConfigTypes configType, Locale locale, Class<O> clazz);

	/**
	 * Creating a reference localization. You will not need to write and read data yourself by accessing the configuration sections.<br>
	 * You will be provided with the object you specified with all the data from the configuration file corresponding to the specified localization.
	 */
	<O extends T> ReferencedLocale<O> createReferencedTranslation(ConfigTypes configType, Locale locale, O object);

	/**
	 * Use this method to get localization if you do not use reference localizations.
	 */
	<L extends PluginLocale> L getSimple(Locale locale) throws ClassCastException;

	/**
	 * Removing localization.
	 */
	<L extends PluginLocale> L remove(Locale locale) throws ClassCastException;

	/**
	 * See {@link List#stream()}
	 */
	<L extends PluginLocale> Stream<L> stream();

	/**
	 * See {@link List#forEach(Consumer)}
	 */
	void forEach(Consumer<? super PluginLocale> action);

	/**
	 * The method returns true if the localization already exists. False if there is no specified localization.
	 */
	boolean contains(Locale locale);

	/**
	 * Getting the number of registered localizations.
	 */
	int size();

	/**
	 * The method will return the value true if no localization is registered, otherwise the value false will be returned.
	 */
	boolean isEmpy();

	/**
	 * Save plugin locales from assets.<br>
	 * This method is automatically called when creating this list.
	 */
	void saveAssetLocales();

	/**
	 * Use this method if you use reference localizations.
	 */
	@SuppressWarnings("unchecked")
	default T getAsReferenced(Locale locale) {
		return (T) getSimple(locale).toReference().get();
	}

	/**
	 * Use this method to get localization if you do not use reference localizations.
	 */
	default <L extends PluginLocale> L getSimple(Player player) {
		return getSimple(player.getEffectiveLocale());
	}

	/**
	 * Use this method if you use reference localizations.
	 */
	default T getAsReferenced(Player player) {
		return getAsReferenced(player.getEffectiveLocale());
	}

	/**
	 * Use this method to get localization if you do not use reference localizations.<br>
	 * The default localization is set by Sponge and is English (USA).
	 */
	default <L extends PluginLocale> L getDefaultSimpleLocale() {
		return getSimple(Locales.DEFAULT);
	}

	/**
	 * Use this method if you use reference localizations.<br>
	 * The default localization is set by Sponge and is English (USA).
	 */
	default T getDefaultAsReferenced() {
		return getAsReferenced(Locales.DEFAULT);
	}

	/**
	 * Use this method to get localization if you do not use reference localizations.<br>
	 * The system localization is set by the configuration of the system on which the server is running and may differ from the default localization. This is convenient for using a separate localization for the console, if there is an appropriate language configuration.
	 */
	default <L extends PluginLocale> L getSystemSimpleLocale() {
		return getSimple(Locale.getDefault());
	}

	/**
	 * Use this method if you use reference localizations.<br>
	 * The system localization is set by the configuration of the system on which the server is running and may differ from the default localization. This is convenient for using a separate localization for the console, if there is an appropriate language configuration.
	 */
	default T getSystemAsReferenced() {
		return getAsReferenced(Locale.getDefault());
	}

	/*
	 * Getting the system locale.<br>
	 * If Sponge does not support your system locale, the default locale for Sponge will be selected.
	 */
	default Locale getSystemOrDefaultLocale() {
		return LocaleService.get().getSystemOrDefaultLocale();
	}

	default T getAsReferenced(CommandSource source) {
		return getAsReferenced(source instanceof Player player ? player.getEffectiveLocale() : getSystemOrDefaultLocale());
	}

}
