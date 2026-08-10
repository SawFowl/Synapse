package sawfowl.synapse.api.services;

import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public interface ServiceProvider {

	/**
	 * Checking whether the service is registered.
	 */
	<S> boolean isExist(Class<S> serviceClass);

	/**
	 * Registration of the service.
	 * 
	 * @param <S> It can be an interface for accessing the service.
	 * @param <I> Service implementation.
	 * @throws RuntimeException If the service is already registered.
	 */
	<S, I extends S> void register(Class<S> serviceClass, I serviceImplement) throws RuntimeException;

	/**
	 * Search for and receive a service if it is registered.
	 */
	<S> Optional<S> find(Class<S> serviceClass);

	/**
	 * Search for and receive a service if it is registered.<br>
	 * Returns `null` if the service is not registered.
	 */
	@Nullable <S> S get(Class<S> serviceClass);

	/**
	 * It is used to get objects of various services. Does not apply to default services.
	 */
	<S> void registerPendingListener(Class<S> serviceClass, Consumer<S> consumer);

}
