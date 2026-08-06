package sawfowl.synapse.api.services;

import java.util.Optional;
import java.util.function.Supplier;

import net.kyori.adventure.builder.AbstractBuilder;
import sawfowl.synapse.api.Synapse;

public interface BuilderService {

	static BuilderService get() {
		return Synapse.getBuilderService();
	}

	<O, T extends AbstractBuilder<O>> boolean isExist(Class<T> builderClass);

	<O, T extends AbstractBuilder<O>> Optional<T> find(Class<T> builderClass);

	<O, T extends AbstractBuilder<O>> T get(Class<T> builderClass);

	<O, T extends AbstractBuilder<O>> void add(Class<T> builderClass, Supplier<? super T> supplier) throws RuntimeException;

}
