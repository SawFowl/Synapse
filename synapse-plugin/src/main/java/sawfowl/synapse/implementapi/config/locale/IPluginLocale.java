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
		return contains(path) ? getRootNode().node(path).getString() : def ? "" : getDefault().getString(path);
	}

	@Override
	public int getInt(Object... path) {
		return contains(path) ? getRootNode().node(path).getInt() : def ? 0 : getDefault().getInt(path);
	}

	@Override
	public long getLong(Object... path) {
		return contains(path) ? getRootNode().node(path).getLong() : def ? 0 : getDefault().getLong(path);
	}

	@Override
	public double getDouble(Object... path) {
		return contains(path) ? getRootNode().node(path).getDouble() : def ? 0 : getDefault().getDouble(path);
	}

	@Override
	public float getFloat(Object... path) {
		return contains(path) ? getRootNode().node(path).getFloat() : def ? 0 : getDefault().getFloat(path);
	}

	@Override
	public boolean getBoolean(Object... path) {
		return contains(path) ? getRootNode().node(path).getBoolean() : def ? false : getDefault().getBoolean(path);
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, Object... path) {
		try {
			return contains(path) ? getRootNode().node(path).getList(clazz) : def ? Collections.emptyList() : getDefault().getList(clazz, path);
		} catch (SerializationException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Nullable
	@Override
	public <T> T getObject(Class<T> clazz, Object... path) {
		try {
			return contains(path) ? getRootNode().node(path).get(clazz) : def ? null : getDefault().getObject(clazz, path);
		} catch (SerializationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private PluginLocale getDefault() {
		return localesList.getDefaultSimpleLocale();
	}

}
