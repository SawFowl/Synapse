package sawfowl.synapse.api.config.text.placeholders;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.config.text.Text;

@FunctionalInterface
public interface Placeholder<T> {

	public Text apply(Text original, T arg, Component def);

}
