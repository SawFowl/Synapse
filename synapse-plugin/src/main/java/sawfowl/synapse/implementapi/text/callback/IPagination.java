package sawfowl.synapse.implementapi.text.callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import sawfowl.synapse.api.text.callback.Pagination;
import sawfowl.synapse.implementapi.services.ICallbackService;

public class IPagination implements Pagination {

	private Component header;
	private Component padding;
	private Component bottom = Component.empty();
	private Component content;
	private Collection<? extends Component> components = Collections.emptyList();
	private int size;
	private int pages;
	private IPagination(){}

	public static IBuilder builder() {
		return new IPagination().createBuilder();
	}

	@Override
	public void sendTo(Audience audience) {
		audience.sendMessage(header.appendNewline().append(content).appendNewline().append(bottom));
	}

	private IBuilder createBuilder() {
		return new IBuilder();
	}

	public class IBuilder implements Builder {

		private IBuilder(){}

		@Override
		public Builder setLinesPerPage(int lines) {
			size = lines;
			return this;
		}

		@Override
		public Builder header(String header, TextColor color) {
			return header(Component.text(header).color(color));
		}

		@Override
		public Builder header(Component header) {
			IPagination.this.header = header;
			return this;
		}

		@Override
		public Builder padding(char padding, TextColor color) {
			IPagination.this.padding = Component.text(padding).color(color);
			return this;
		}

		@Override
		public Builder content(Collection<? extends Component> components) {
			IPagination.this.components = components;
			return this;
		}

		private Builder content(Collection<? extends Component> components, int page, IPagination previous) {
			if(components.size() / size > 0) {
				List<Component> copy = new ArrayList<>(components);
				int i = size;
				for(Component component : components) {
					if(i == 0) break;
					copy.remove(0);
					if(content == null) {
						content = component.appendNewline();
					} else content = i > 1 ? content.append(component).appendNewline() : content.append(component);
					i--;
				}
				createFooter(page == 1 ? null : previous, copy.isEmpty() ? null : next(copy, IPagination.this, page + 1), page);
			} else {
				content = Component.join(JoinConfiguration.newlines(), components);
				createFooter(pages > 1 ? previous : null, null, page);
			}
			return this;
		}

		private IPagination next(Collection<Component> components, IPagination previous, int page) {
			IPagination pagination = new IPagination();
			IBuilder builder = pagination.createBuilder();
			builder.setLinesPerPage(size);
			pagination.pages = IPagination.this.pages;
			pagination.header = IPagination.this.header;
			pagination.padding = IPagination.this.padding;
			List<Component> copy = new ArrayList<>(components);
			builder.content(copy, page, IPagination.this);
			return pagination;
		}

		private void createFooter(IPagination previous, IPagination next, int page) {
			if(previous == null && next == null) {
				for(int i = 0; i < 52 ; i++) bottom = bottom.append(padding);
				return;
			} else {
				Component part = Component.empty();
				Component back = Component.text("<<").color(padding.color());
				Component nextpage = Component.text(">>").color(padding.color());
				if(previous != null) back = back.color(invertColor(padding.color())).clickEvent(ICallbackService.getInstance().paginationOf(s -> previous.sendTo(s)));
				if(next != null) nextpage = nextpage.color(invertColor(padding.color())).clickEvent(ICallbackService.getInstance().paginationOf(s -> next.sendTo(s)));
				back = Component.text(" ").append(back).append(Component.text(" "));
				nextpage = Component.text(" ").append(nextpage).append(Component.text(" "));
				Component current = Component.text(page).color(pageColor(padding.color())).append(Component.text("/").color(NamedTextColor.WHITE)).append(Component.text(pages).color(pagesColor(padding.color())));
				Component listing = Component.empty().append(back).append(current).append(nextpage);
				for(int i = 0; i < 33 - length(listing) ; i++) part = part.append(padding);
				bottom = bottom.append(part).append(listing).append(part);
			}
			
		}

		@Override
		public Pagination build() {
			if(padding != null) {
				int headerLength = length(header) + 2;
				Component part = padding;
				while((length(part) * 2) + headerLength < 51) {
					part = part.append(padding);
				}
				IPagination.this.header = part.append(Component.text(" ").append(header).append(Component.text(" "))).append(part);
			}
			double p = (double) components.size() / (double) size;
			pages = components.size() / size;
			if(((double) pages) < p) pages++;
			content(components, 1, IPagination.this);
			components = Collections.emptyList();
			return IPagination.this;
		}

	}

	private int length(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component).length();
	}

	private TextColor invertColor(TextColor color) {
		return TextColor.color(255 - color.red(), 255 - color.green(), 255 - color.blue());
	}

	private TextColor pageColor(TextColor color) {
		return TextColor.color(255 - ((255 - color.red()) / 3), 255 - ((255 - color.green()) / 3), 255 - ((255 - color.blue()) / 3));
	}

	private TextColor pagesColor(TextColor color) {
		return TextColor.color(255 - (int) ((double) (255 - color.red()) / 1.2), 255 - (int) ((double) (255 - color.green()) / 1.2), 255 - (int) ((double) (255 - color.blue()) / 1.2));
	}

}
