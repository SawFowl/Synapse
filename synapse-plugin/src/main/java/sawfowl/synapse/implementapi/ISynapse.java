package sawfowl.synapse.implementapi;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.services.LocaleService;

public class ISynapse extends Synapse {

	private final SynapsePlugin plugin;
	private LocaleService localeService;
	public ISynapse(SynapsePlugin plugin) {
		this.plugin = plugin;
	}
	@Override
	public LocaleService getLocaleService() {
		return localeService;
	}

}
