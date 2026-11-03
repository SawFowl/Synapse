package sawfowl.synapse.implementapi.config.locale;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;

import org.spongepowered.configurate.serialize.SerializationException;

import sawfowl.synapse.api.Locales;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.locale.LocalesList;
import sawfowl.synapse.api.config.locale.PluginLocale;
import sawfowl.synapse.api.config.locale.ReferencedLocale;
import sawfowl.synapse.api.config.locale.Translation;
import sawfowl.synapse.implementapi.config.IConfig;

public class IPluginLocale extends IConfig implements PluginLocale {

	public static final IPluginLocale create(Path configDir, ConfigTypes configType, Locale locale, LocalesList<? extends Translation> localesList) {
		return new IPluginLocale(configDir, configType, locale, localesList);
	}

	private final Locale locale;
	private final LocalesList<? extends Translation> localesList;
	private final boolean def;
	private IPluginLocale(Path configDir, ConfigTypes configType, Locale locale, LocalesList<? extends Translation> localesList) {
		super(configDir, locale.toLanguageTag(), configType, null);
		this.locale = locale;
		def = locale.equals(Locales.DEFAULT);
		this.localesList = localesList;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Translation, O extends ReferencedLocale<T>> O toReferenceTranslation(T config) {
		Objects.requireNonNull(config);
		localesList.remove(locale);
		return (O) ((LocalesList<T>) localesList).createReferencedTranslation(getType(), locale, config);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Translation, O extends ReferencedLocale<T>> O toReferenceTranslation(Class<T> config) {
		localesList.remove(locale);
		return (O) ((LocalesList<T>) localesList).createReferencedTranslation(getType(), locale, config);
	}

	@Override
	public String getString(Object... path) {
		return contains(path) ? getRootNode().node(path).getString() : (fallback() == null ? "" : fallback().getString(path));
	}

	@Override
	public int getInt(Object... path) {
		return contains(path) ? getRootNode().node(path).getInt() : (fallback() == null ? 0 : fallback().getInt(path));
	}

	@Override
	public long getLong(Object... path) {
		return contains(path) ? getRootNode().node(path).getLong() : (fallback() == null ? 0 : fallback().getLong(path));
	}

	@Override
	public double getDouble(Object... path) {
		return contains(path) ? getRootNode().node(path).getDouble() : (fallback() == null ? 0 : fallback().getDouble(path));
	}

	@Override
	public float getFloat(Object... path) {
		return contains(path) ? getRootNode().node(path).getFloat() : (fallback() == null ? 0 : fallback().getFloat(path));
	}

	@Override
	public boolean getBoolean(Object... path) {
		return contains(path) ? getRootNode().node(path).getBoolean() : (fallback() == null ? false : fallback().getBoolean(path));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, Object... path) {
		try {
			return contains(path) ? getRootNode().node(path).getList(clazz) : (fallback() == null ? Collections.emptyList() : fallback().getList(clazz, path));
		} catch (SerializationException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Nullable
	@Override
	public <T> T getObject(Class<T> clazz, Object... path) {
		try {
			return contains(path) ? getRootNode().node(path).get(clazz) : (fallback() == null ? null : fallback().getObject(clazz, path));
		} catch (SerializationException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * The default locale to take a missing value from, or {@code null} if this is it
	 * or it is not loaded (the getters used to throw NPE then).
	 */
	private PluginLocale fallback() {
		if(def) return null;
		PluginLocale fallback = localesList.getDefaultSimpleLocale();
		return fallback == this ? null : fallback;
	}

}
