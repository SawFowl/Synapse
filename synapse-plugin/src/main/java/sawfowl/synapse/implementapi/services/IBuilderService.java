package sawfowl.synapse.implementapi.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import net.kyori.adventure.builder.AbstractBuilder;
import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.text.callback.Pagination;
import sawfowl.synapse.implementapi.text.IText;
import sawfowl.synapse.implementapi.text.callback.IPagination;

public class IBuilderService implements BuilderService {

	private Map<Class<?>, Supplier<?>> builders = new HashMap<>();
	public IBuilderService() {
		add(Text.Builder.class, () -> IText.builder());
		add(Pagination.Builder.class, () -> IPagination.builder());
	}

	@Override
	public <O, T extends AbstractBuilder<O>> boolean isExist(Class<T> builderClass) {
		return builders.containsKey(builderClass);
	}

	@Override
	public <O, T extends AbstractBuilder<O>> Optional<T> find(Class<T> builderClass) {
		return Optional.ofNullable(get(builderClass));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O, T extends AbstractBuilder<O>> T get(Class<T> builderClass) {
		return isExist(builderClass) ? (T) builders.get(builderClass).get() : null;
	}

	@Override
	public <O, T extends AbstractBuilder<O>> void add(Class<T> builderClass, Supplier<? super T> supplier) {
		if(isExist(builderClass)) throw new RuntimeException("The '" + builderClass.getName() +  "' builder has already been registered.");
		builders.put(builderClass, supplier);
	}

}
