package sawfowl.synapse.api.services;

import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.Synapse;

/**
 * A service for creating simple loggers with support for transforming color codes.
 * 
 * @author SawFowl
 */
public interface LoggerService {

	static LoggerService get() {
		return Synapse.getInstance().getServiceProvider().get(LoggerService.class);
	}

	Logger createApacheLogger(String name);

	Logger createJavaLogger(String name);

}
