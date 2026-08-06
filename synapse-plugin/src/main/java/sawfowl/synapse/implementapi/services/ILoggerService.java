package sawfowl.synapse.implementapi.services;

import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.services.LoggerService;
import sawfowl.synapse.implementapi.logger.IApacheLogger;
import sawfowl.synapse.implementapi.logger.IJavaLogger;

public class ILoggerService implements LoggerService {

	private static final ILoggerService INSTANCE = new ILoggerService();

	public static ILoggerService getInstance() {
		return INSTANCE;
	}

	@Override
	public Logger createApacheLogger(String name) {
		return new IApacheLogger(name);
	}

	@Override
	public Logger createJavaLogger(String name) {
		return new IJavaLogger(name);
	}

}
