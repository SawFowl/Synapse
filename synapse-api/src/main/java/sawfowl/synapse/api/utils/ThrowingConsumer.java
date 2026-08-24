package sawfowl.synapse.api.utils;

import java.util.function.Consumer;

/**
 * @author SawFowl
 */
public interface ThrowingConsumer<T, E extends Exception> {

	int accept(T t) throws E;

	static <T, E extends Exception> Consumer<T> unchecked(ThrowingConsumer<T, E> consumer) {
		return 
		(t) -> {
			try {
				consumer.accept(t);
			} catch (Exception e) {
			}
		};
	}

}
