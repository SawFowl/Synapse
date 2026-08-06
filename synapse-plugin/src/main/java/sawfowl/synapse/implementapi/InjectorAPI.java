package sawfowl.synapse.implementapi;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import sawfowl.synapse.api.Synapse;

public class InjectorAPI extends AbstractModule {

	private final Synapse synapse;
	public InjectorAPI(Synapse synapse) {
		this.synapse = synapse;
	}

	public Injector createInjector() {
		return Guice.createInjector(this);
	}

	@Override
	protected void configure() {
		bind(Synapse.class).toInstance(synapse);
		this.requestStaticInjection(Synapse.class);
	}

}
