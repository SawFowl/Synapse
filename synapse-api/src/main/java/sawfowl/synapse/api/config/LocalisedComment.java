package sawfowl.synapse.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface to implement localised comments in plugin configuration.<br>
 * The required comment will be taken from the language configuration<br>
 * with the localisation of the corresponding system one.<br>
 * If no such configuration is found, or it does not contain a string at the specified path,<br>
 * an attempt will be made to retrieve that string from the default configuration.<br>
 * If the default localisation does not have the required section,<br>
 * a stub string will be returned indicating a path error.<br><br>
 * <b>Use only for the basic configuration files of the plugin!</b>
 * 
 * @author SawFowl
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LocalisedComment {

	/**
	 * Path to the localisation configuration section containing the string with the required comment.
	 */
	String[] path();

	/**
	 * Identifier of the plugin in the localisation of which it is necessary to find a line for setting a comment in the configuration.
	 */
	String plugin();

	String def() default "";

}
