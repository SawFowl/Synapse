package sawfowl.synapse.api.text;

import net.kyori.adventure.text.Component;

@FunctionalInterface
public interface Placeholder<T> {

	public Text apply(Text original, T arg, Component def);

}
