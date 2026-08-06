package sawfowl.synapse.implementapi.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.velocitypowered.api.proxy.ProxyServer;

import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.CallbackSevice;
import sawfowl.synapse.api.services.ConfigurationService;
import sawfowl.synapse.api.services.LocaleService;
import sawfowl.synapse.api.services.LoggerService;
import sawfowl.synapse.api.services.PlaceholderService;
import sawfowl.synapse.api.services.ServiceProvider;
import sawfowl.synapse.implementapi.text.callback.ICallbackService;

public class IServiceProvider implements ServiceProvider {

	private Map<Class<?>, Object> services = new HashMap<>();
	public IServiceProvider(ProxyServer server) {
		register(ProxyServer.class, server);
		register(LoggerService.class, new ILoggerService());
		register(BuilderService.class, new IBuilderService());
		register(CallbackSevice.class, new ICallbackService());
		register(LocaleService.class, ILocaleService.getInstance());
		register(ConfigurationService.class, IConfigurationService.getInstance());
		register(PlaceholderService.class, new IPlaceholderService());
	}

	@Override
	public <S> boolean isExist(Class<S> serviceClass) {
		return services.containsKey(serviceClass);
	}

	@Override
	public <S, I extends S> void register(Class<S> serviceClass, I serviceImplement) {
		if(isExist(serviceClass)) throw new RuntimeException("The '" + serviceClass.getName() + "' service is already registered.");
		services.put(serviceClass, serviceImplement);
	}

	@Override
	public <S> Optional<S> find(Class<S> serviceClass) {
		return Optional.ofNullable(get(serviceClass));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> S get(Class<S> serviceClass) {
		return isExist(serviceClass) ? (S) services.get(serviceClass) : null;
	}

}
