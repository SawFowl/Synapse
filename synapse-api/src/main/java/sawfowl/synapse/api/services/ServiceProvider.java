package sawfowl.synapse.api.services;

import java.util.Optional;
import java.util.function.Consumer;

public interface ServiceProvider {

	<S> boolean isExist(Class<S> serviceClass);

	<S, I extends S> void register(Class<S> serviceClass, I serviceImplement) throws RuntimeException;

	<S> Optional<S> find(Class<S> serviceClass);

	<S> S get(Class<S> serviceClass);

	<S> void registerPendingListener(Class<S> serviceClass, Consumer<S> consumer);

}
