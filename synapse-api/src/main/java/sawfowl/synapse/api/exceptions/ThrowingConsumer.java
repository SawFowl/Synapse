package sawfowl.synapse.api.exceptions;

import java.util.function.Consumer;

public interface ThrowingConsumer<T, E extends Exception> {
	void accept(T t) throws E;

	static <T, E extends Exception> Consumer<T> unchecked(ThrowingConsumer<T, E> consumer) {
	return (t) -> {
			try {
				consumer.accept(t);
			} catch (Exception e) {
			}
		};
	}

}
