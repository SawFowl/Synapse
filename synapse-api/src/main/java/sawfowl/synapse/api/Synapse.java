package sawfowl.synapse.api;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;

import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.CallbackSevice;
import sawfowl.synapse.api.services.ConfigurationService;
import sawfowl.synapse.api.services.LocaleService;
import sawfowl.synapse.api.services.PlaceholderService;
import sawfowl.synapse.api.services.ServiceProvider;

public abstract class Synapse {

	@Inject
	private static Synapse INSTANCE;

	public static Synapse getInstance() {
		return INSTANCE;
	}

	public static ProxyServer getProxy() {
		return getInstance().getServiceProvider().get(ProxyServer.class);
	}

	public static CallbackSevice getCallbackSevice() {
		return getInstance().getServiceProvider().get(CallbackSevice.class);
	}

	public static BuilderService getBuilderService() {
		return getInstance().getServiceProvider().get(BuilderService.class);
	}

	public static LocaleService getLocaleService() {
		return getInstance().getServiceProvider().get(LocaleService.class);
	}

	public static ConfigurationService getConfigurationService() {
		return getInstance().getServiceProvider().get(ConfigurationService.class);
	}

	public static PlaceholderService getPlaceholderService() {
		return getInstance().getServiceProvider().get(PlaceholderService.class);
	}

	public abstract ServiceProvider getServiceProvider();

}
