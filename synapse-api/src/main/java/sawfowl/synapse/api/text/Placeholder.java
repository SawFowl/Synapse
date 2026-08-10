package sawfowl.synapse.api.text;

import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;

@FunctionalInterface
public interface Placeholder<T> {

	/**
	 * Applying a placeholder to a text.
	 * 
	 * @param original - The original message in which the text should be replaced.
	 * @param arg - The data source for text replacement.
	 * @param alt - An alternative text that can be set when replacing.
	 * @return
	 */
	public Text apply(Text original, @Nullable T arg, Component alt);

}
