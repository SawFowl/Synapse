package sawfowl.synapse.api;

import com.google.inject.Inject;

import sawfowl.synapse.api.services.LocaleService;

public abstract class Synapse {

	@Inject
	private static Synapse INSTANCE;

	public static Synapse getInstance() {
		return INSTANCE;
	}

	public abstract LocaleService getLocaleService();

}
