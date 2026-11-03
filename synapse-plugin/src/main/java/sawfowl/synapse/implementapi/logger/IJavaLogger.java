package sawfowl.synapse.implementapi.logger;

public class IJavaLogger extends AbstractLogger {

	private final System.Logger logger;
	public IJavaLogger(String name) {
		logger = System.getLogger(name + Colors.CLEAR);
	}

	@Override
	protected boolean isEnabled(Level level) {
		return logger.isLoggable(toJava(level));
	}

	@Override
	protected void write(Level level, String message, Throwable throwable) {
		// With the throwable, not its message only: the stack trace used to be lost.
		if(throwable == null) {
			logger.log(toJava(level), message);
		} else logger.log(toJava(level), message, throwable);
	}

	private static System.Logger.Level toJava(Level level) {
		return switch (level) {
			case INFO -> System.Logger.Level.INFO;
			case WARN -> System.Logger.Level.WARNING;
			case ERROR -> System.Logger.Level.ERROR;
			case DEBUG -> System.Logger.Level.DEBUG;
		};
	}

}
