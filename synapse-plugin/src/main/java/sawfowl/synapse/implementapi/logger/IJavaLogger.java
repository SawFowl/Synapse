package sawfowl.synapse.implementapi.logger;

import java.lang.System.Logger.Level;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.text.Text;

public class IJavaLogger implements Logger {

	private java.lang.System.Logger logger;
	public IJavaLogger(String name) {
		logger = System.getLogger(name + Colors.CLEAR);
	}

	@Override
	public void info(Object object) {
		logger.log(Level.INFO, Colors.convertColors(object));
	}

	@Override
	public void info(Text text) {
		logger.log(Level.INFO, Colors.convertColors(text));
	}

	@Override
	public void info(Object object, Throwable throwable) {
		logger.log(Level.INFO, Colors.convertColors(object) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void info(String string) {
		logger.log(Level.INFO, Colors.convertColors(string));
	}

	@Override
	public void info(String string, Throwable throwable) {
		logger.log(Level.INFO, Colors.convertColors(string) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void info(Component component) {
		info(Colors.convertColors(component));
	}

	@Override
	public void info(Component component, Throwable throwable) {
		info(Colors.convertColors(component) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void warn(Object object) {
		logger.log(Level.WARNING, Colors.convertColors(object));
	}

	@Override
	public void warn(Text text) {
		logger.log(Level.WARNING, Colors.convertColors(text));
	}

	@Override
	public void warn(Object object, Throwable throwable) {
		logger.log(Level.WARNING, Colors.convertColors(object) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void warn(String string) {
		logger.log(Level.WARNING, string);
	}

	@Override
	public void warn(String string, Throwable throwable) {
		logger.log(Level.WARNING, Colors.convertColors(string) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void warn(Component component) {
		warn(Colors.convertColors(component));
	}

	@Override
	public void warn(Component component, Throwable throwable) {
		warn(Colors.convertColors(component) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void error(Object object) {
		logger.log(Level.ERROR, Colors.convertColors(object));
	}

	@Override
	public void error(Text text) {
		logger.log(Level.ERROR, Colors.convertColors(text));
	}

	@Override
	public void error(Object object, Throwable throwable) {
		logger.log(Level.ERROR, Colors.convertColors(object) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void error(String string) {
		logger.log(Level.ERROR, Colors.convertColors(string));
	}

	@Override
	public void error(String string, Throwable throwable) {
		logger.log(Level.ERROR, Colors.convertColors(string) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void error(Component component) {
		error(Colors.convertColors(component));
	}

	@Override
	public void error(Component component, Throwable throwable) {
		error(Colors.convertColors(component) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void debug(Object object) {
		logger.log(Level.DEBUG, Colors.convertColors(object));
	}

	@Override
	public void debug(Text text) {
		logger.log(Level.DEBUG, Colors.convertColors(text));
	}

	@Override
	public void debug(Object object, Throwable throwable) {
		logger.log(Level.DEBUG, Colors.convertColors(object) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void debug(String string) {
		logger.log(Level.DEBUG, Colors.convertColors(string));
	}

	@Override
	public void debug(String string, Throwable throwable) {
		logger.log(Level.DEBUG, Colors.convertColors(string) + "\n" + throwable.getLocalizedMessage());
	}

	@Override
	public void debug(Component component) {
		debug(Colors.convertColors(component));
	}

	@Override
	public void debug(Component component, Throwable throwable) {
		debug(Colors.convertColors(component) + "\n" + throwable.getLocalizedMessage());
	}

}
