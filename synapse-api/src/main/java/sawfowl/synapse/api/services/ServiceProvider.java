package sawfowl.synapse.api.services;

import java.util.Optional;

public interface ServiceProvider {

	<S> boolean isExist(Class<S> serviceClass);

	<S, I extends S> void register(Class<S> serviceClass, I serviceImplement) throws RuntimeException;

	<S> Optional<S> find(Class<S> serviceClass);

	<S> S get(Class<S> serviceClass);

}
