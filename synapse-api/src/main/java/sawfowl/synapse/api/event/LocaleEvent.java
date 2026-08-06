package sawfowl.synapse.api.event;

import java.util.Locale;

import sawfowl.synapse.api.config.locale.PluginLocale;

public interface LocaleEvent {

	String getPluginId();

	Locale getLocale();

	interface Delete extends LocaleEvent {

		String getFileName();
		
	}

	interface Create extends LocaleEvent {

		PluginLocale getLocaleConfig();

		String configType();

	}

	interface Reload extends LocaleEvent {

		PluginLocale getLocaleConfig();

	}

}
