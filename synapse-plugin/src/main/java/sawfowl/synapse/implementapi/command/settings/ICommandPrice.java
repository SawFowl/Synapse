package sawfowl.synapse.implementapi.command.settings;

import java.math.BigDecimal;
import java.util.Objects;

import sawfowl.synapse.api.commands.settings.CommandPrice;
import sawfowl.synapse.api.economy.Currency;

public class ICommandPrice implements CommandPrice {

	public static Builder builder() {
		return new ICommandPrice().createBuilder();
	}

	private Currency currency;
	private BigDecimal price = BigDecimal.ZERO;
	private String ignorePermission;
	private ICommandPrice() {}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public String getIgnorePermission() {
		return ignorePermission;
	}

	private Builder createBuilder() {
		return new IBuilder();
	}

	private class IBuilder implements Builder {

		@Override
		public CommandPrice build() {
			return ICommandPrice.this;
		}

		@Override
		public CommandPrice of(Currency currency, BigDecimal price, String ignorePermission) {
			Objects.requireNonNull(currency);
			Objects.requireNonNull(price);
			ICommandPrice.this.currency = currency;
			ICommandPrice.this.price = price;
			ICommandPrice.this.ignorePermission = ignorePermission;
			return build();
		}
		
	}

}
