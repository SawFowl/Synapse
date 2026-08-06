package sawfowl.synapse.implementapi;

import com.velocitypowered.api.proxy.ProxyServer;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.services.ServiceProvider;
import sawfowl.synapse.implementapi.services.IServiceProvider;

public class ISynapse extends Synapse {

	private final ServiceProvider serviceProvider;
	public ISynapse(ProxyServer proxy) {
		serviceProvider = new IServiceProvider(proxy);
	}

	@Override
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

}
