package sawfowl.synapse.implementapi.logger;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.text.Text;

/**
 * The 28 methods of {@link Logger} in one place; a backend only writes a prepared line.<br>
 * The two loggers were 157-line copies of each other that had already drifted apart:
 * the Java one dropped stack traces and did not convert the colors of {@code warn(String)}.
 */
abstract class AbstractLogger implements Logger {

	enum Level {
		INFO, WARN, ERROR, DEBUG
	}

	protected abstract boolean isEnabled(Level level);

	protected abstract void write(Level level, String message, Throwable throwable);

	private void log(Level level, Object message, Throwable throwable) {
		// The colors are converted only if the line will be written (debug is usually off).
		if(isEnabled(level)) write(level, Colors.convertColors(message), throwable);
	}

	@Override
	public void info(Object object) {
		log(Level.INFO, object, null);
	}

	@Override
	public void info(Text text) {
		log(Level.INFO, text, null);
	}

	@Override
	public void info(Object object, Throwable throwable) {
		log(Level.INFO, object, throwable);
	}

	@Override
	public void info(String string) {
		log(Level.INFO, string, null);
	}

	@Override
	public void info(String string, Throwable throwable) {
		log(Level.INFO, string, throwable);
	}

	@Override
	public void info(Component component) {
		log(Level.INFO, component, null);
	}

	@Override
	public void info(Component component, Throwable throwable) {
		log(Level.INFO, component, throwable);
	}

	@Override
	public void warn(Object object) {
		log(Level.WARN, object, null);
	}

	@Override
	public void warn(Text text) {
		log(Level.WARN, text, null);
	}

	@Override
	public void warn(Object object, Throwable throwable) {
		log(Level.WARN, object, throwable);
	}

	@Override
	public void warn(String string) {
		log(Level.WARN, string, null);
	}

	@Override
	public void warn(String string, Throwable throwable) {
		log(Level.WARN, string, throwable);
	}

	@Override
	public void warn(Component component) {
		log(Level.WARN, component, null);
	}

	@Override
	public void warn(Component component, Throwable throwable) {
		log(Level.WARN, component, throwable);
	}

	@Override
	public void error(Object object) {
		log(Level.ERROR, object, null);
	}

	@Override
	public void error(Text text) {
		log(Level.ERROR, text, null);
	}

	@Override
	public void error(Object object, Throwable throwable) {
		log(Level.ERROR, object, throwable);
	}

	@Override
	public void error(String string) {
		log(Level.ERROR, string, null);
	}

	@Override
	public void error(String string, Throwable throwable) {
		log(Level.ERROR, string, throwable);
	}

	@Override
	public void error(Component component) {
		log(Level.ERROR, component, null);
	}

	@Override
	public void error(Component component, Throwable throwable) {
		log(Level.ERROR, component, throwable);
	}

	@Override
	public void debug(Object object) {
		log(Level.DEBUG, object, null);
	}

	@Override
	public void debug(Text text) {
		log(Level.DEBUG, text, null);
	}

	@Override
	public void debug(Object object, Throwable throwable) {
		log(Level.DEBUG, object, throwable);
	}

	@Override
	public void debug(String string) {
		log(Level.DEBUG, string, null);
	}

	@Override
	public void debug(String string, Throwable throwable) {
		log(Level.DEBUG, string, throwable);
	}

	@Override
	public void debug(Component component) {
		log(Level.DEBUG, component, null);
	}

	@Override
	public void debug(Component component, Throwable throwable) {
		log(Level.DEBUG, component, throwable);
	}

}
