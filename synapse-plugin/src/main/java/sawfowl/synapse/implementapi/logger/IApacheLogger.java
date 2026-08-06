package sawfowl.synapse.implementapi.logger;

import org.apache.logging.log4j.LogManager;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.text.Text;

public class IApacheLogger implements Logger {

	private org.apache.logging.log4j.Logger logger;
	public IApacheLogger(String name) {
		logger = LogManager.getLogger(name + Colors.CLEAR);
	}

	@Override
	public void info(Object object) {
		logger.info(Colors.convertColors(object));
	}

	@Override
	public void info(Text text) {
		logger.info(Colors.convertColors(text));
	}

	@Override
	public void info(Object object, Throwable throwable) {
		logger.info(Colors.convertColors(object), throwable);
	}

	@Override
	public void info(String string) {
		logger.info(Colors.convertColors(string));
	}

	@Override
	public void info(String string, Throwable throwable) {
		logger.info(Colors.convertColors(string), throwable);
	}

	@Override
	public void info(Component component) {
		info(Colors.convertColors(component));
	}

	@Override
	public void info(Component component, Throwable throwable) {
		info(Colors.convertColors(component), throwable);
	}

	@Override
	public void warn(Object object) {
		logger.warn(Colors.convertColors(object));
	}

	@Override
	public void warn(Text text) {
		logger.warn(Colors.convertColors(text));
	}

	@Override
	public void warn(Object object, Throwable throwable) {
		logger.warn(Colors.convertColors(object), throwable);
	}

	@Override
	public void warn(String string) {
		logger.warn(Colors.convertColors(string));
	}

	@Override
	public void warn(String string, Throwable throwable) {
		logger.warn(Colors.convertColors(string), throwable);
	}

	@Override
	public void warn(Component component) {
		warn(Colors.convertColors(component));
	}

	@Override
	public void warn(Component component, Throwable throwable) {
		warn(Colors.convertColors(component), throwable);
	}

	@Override
	public void error(Object object) {
		logger.error(Colors.convertColors(object));
	}

	@Override
	public void error(Text text) {
		logger.error(Colors.convertColors(text));
	}

	@Override
	public void error(Object object, Throwable throwable) {
		logger.error(Colors.convertColors(object), throwable);
	}

	@Override
	public void error(String string) {
		logger.error(Colors.convertColors(string));
	}

	@Override
	public void error(String string, Throwable throwable) {
		logger.error(Colors.convertColors(string), throwable);
	}

	@Override
	public void error(Component component) {
		error(Colors.convertColors(component));
	}

	@Override
	public void error(Component component, Throwable throwable) {
		error(Colors.convertColors(component), throwable);
	}

	@Override
	public void debug(Object object) {
		logger.debug(Colors.convertColors(object));
	}

	@Override
	public void debug(Text text) {
		logger.debug(Colors.convertColors(text));
	}

	@Override
	public void debug(Object object, Throwable throwable) {
		logger.debug(Colors.convertColors(object), throwable);
	}

	@Override
	public void debug(String string) {
		logger.debug(Colors.convertColors(string));
	}

	@Override
	public void debug(String string, Throwable throwable) {
		logger.debug(Colors.convertColors(string), throwable);
	}

	@Override
	public void debug(Component component) {
		debug(Colors.convertColors(component));
	}

	@Override
	public void debug(Component component, Throwable throwable) {
		debug(Colors.convertColors(component), throwable);
	}

}
