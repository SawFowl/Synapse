package sawfowl.synapse.implementapi.config.locale;

import java.nio.file.Path;
import java.util.Locale;

import org.spongepowered.configurate.ConfigurationNode;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.locale.ReferencedLocale;
import sawfowl.synapse.api.config.locale.Translation;
import sawfowl.synapse.implementapi.config.IReferencedConfig;

public class IReferencedLocale<T extends Translation> extends IReferencedConfig<T, ConfigurationNode> implements ReferencedLocale<T> {

	public static final <T extends Translation> IReferencedLocale<T> create(Path configDir, ConfigTypes configType, Class<T> clazz, Locale locale) {
		return new IReferencedLocale<T>(configDir, configType, clazz, locale);
	}

	public static final <T extends Translation> IReferencedLocale<T> create(Path configDir, ConfigTypes configType, T object, Locale locale) {
		return new IReferencedLocale<T>(configDir, configType, object, locale);
	}

	private final Locale locale;
	private IReferencedLocale(Path configDir, ConfigTypes configType, Class<T> clazz, Locale locale) {
		super(configDir, locale.toLanguageTag(), configType, null, clazz);
		this.locale = locale;
	}

	private IReferencedLocale(Path configDir, ConfigTypes configType, T object, Locale locale) {
		super(configDir, locale.toLanguageTag(), configType, null, object);
		this.locale = locale;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <L extends Translation, O extends ReferencedLocale<L>> O toReferenceTranslation(L config) {
		return (O) this;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <L extends Translation, O extends ReferencedLocale<L>> O toReferenceTranslation(Class<L> config) {
		return (O) this;
	}

}
