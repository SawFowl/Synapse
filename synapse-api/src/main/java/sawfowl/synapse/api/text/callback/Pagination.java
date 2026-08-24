package sawfowl.synapse.api.text.callback;

import java.util.Collection;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import sawfowl.synapse.api.Synapse;

/**
 * Using this interface, you can create multi‑page menus in the chat.
 * 
 * @author SawFowl
 */
public interface Pagination {

	/**
	 * @param linesPerPage - Do not specify a value less than 1. This may cause a failure.
	 */
	static Builder builder(int linesPerPage) {
		return Synapse.getBuilderService().get(Builder.class).setLinesPerPage(linesPerPage);
	}

	void sendTo(Audience audience);

	public interface Builder extends AbstractBuilder<Pagination> {

		Builder setLinesPerPage(int lines);

		Builder header(String header, TextColor color);

		Builder header(Component header);

		Builder padding(char padding, TextColor color);

		Builder content(Collection<? extends Component> components);

		Pagination build();

	}

}
