package sawfowl.synapse.implementapi.logger;

import org.apache.logging.log4j.LogManager;

public class IApacheLogger extends AbstractLogger {

	private final org.apache.logging.log4j.Logger logger;
	public IApacheLogger(String name) {
		logger = LogManager.getLogger(name + Colors.CLEAR);
	}

	@Override
	protected boolean isEnabled(Level level) {
		return logger.isEnabled(toApache(level));
	}

	@Override
	protected void write(Level level, String message, Throwable throwable) {
		logger.log(toApache(level), message, throwable);
	}

	private static org.apache.logging.log4j.Level toApache(Level level) {
		return switch (level) {
			case INFO -> org.apache.logging.log4j.Level.INFO;
			case WARN -> org.apache.logging.log4j.Level.WARN;
			case ERROR -> org.apache.logging.log4j.Level.ERROR;
			case DEBUG -> org.apache.logging.log4j.Level.DEBUG;
		};
	}

}
