package sawfowl.synapse.api;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.services.LoggerService;
import sawfowl.synapse.api.text.Text;

public interface Logger {

	static Logger createApacheLogger(String name) {
		return LoggerService.get().createApacheLogger(name);
	}

	static Logger createJavaLogger(String name) {
		return LoggerService.get().createJavaLogger(name);
	}

	/**
	 * Logs a message object with the {@link Level#INFO INFO} level.
	 */
	void info(Object object);

	/**
	 * Logs a message object with the {@link Level#INFO INFO} level.
	 */
	void info(Text text);

	/**
	 * Logs a message at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void info(Object object, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#INFO INFO} level.
	 */
	void info(String string);

	/*
	 * Logs a message at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void info(String string, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#INFO INFO} level.
	 */
	void info(Component component);

	/*
	 * Logs a message at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void info(Component component, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#WARN WARN} level.
	 */
	void warn(Object object);

	/**
	 * Logs a message object with the {@link Level#WARN WARN} level.
	 */
	void warn(Text text);

	/**
	 * Logs a message at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void warn(Object object, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#WARN WARN} level.
	 */
	void warn(String string);

	/**
	 * Logs a message at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void warn(String string, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#WARN WARN} level.
	 */
	void warn(Component component);

	/**
	 * Logs a message at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void warn(Component component, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#ERROR ERROR} level.
	 */
	void error(Object object);

	/**
	 * Logs a message object with the {@link Level#ERROR ERROR} level.
	 */
	void error(Text text);

	/**
	 * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void error(Object object, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#ERROR ERROR} level.
	 */
	void error(String string);

	/**
	 * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void error(String string, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#ERROR ERROR} level.
	 */
	void error(Component component);

	/**
	 * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void error(Component component, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#DEBUG DEBUG} level.
	 */
	void debug(Object object);

	/**
	 * Logs a message object with the {@link Level#DEBUG DEBUG} level.
	 */
	void debug(Text text);

	/**
	 * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void debug(Object object, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#DEBUG DEBUG} level.
	 */
	void debug(String string);

	/**
	 * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void debug(String string, Throwable throwable);

	/**
	 * Logs a message object with the {@link Level#DEBUG DEBUG} level.
	 */
	void debug(Component component);

	/**
	 * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
	 * <code>throwable</code> passed as parameter.
	 */
	void debug(Component component, Throwable throwable);

}
