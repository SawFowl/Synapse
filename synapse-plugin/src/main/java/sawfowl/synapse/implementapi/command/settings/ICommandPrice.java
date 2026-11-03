package sawfowl.synapse.implementapi.command.settings;

import java.math.BigDecimal;
import java.util.Objects;

import sawfowl.synapse.api.ResourceKey;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.settings.CommandPrice;
import sawfowl.synapse.api.economy.Currency;
import sawfowl.synapse.api.services.EconomyService;

public class ICommandPrice implements CommandPrice {

	public static Builder builder() {
		return new ICommandPrice().createBuilder();
	}

	/**
	 * A price from a config: the currency is found by its key when it is first needed,
	 * because the economy service may not exist yet when the config is loaded.
	 */
	public static ICommandPrice lazy(String currencyKey, BigDecimal price, String ignorePermission) {
		ICommandPrice result = new ICommandPrice();
		result.currencyKey = currencyKey;
		result.price = price == null ? BigDecimal.ZERO : price;
		result.ignorePermission = ignorePermission;
		return result;
	}

	private volatile Currency currency;
	private String currencyKey;
	private BigDecimal price = BigDecimal.ZERO;
	private String ignorePermission;
	private ICommandPrice() {}

	/**
	 * @return the currency, or {@code null} if there is no economy or no currency with the key from the config.
	 */
	@Override
	public Currency getCurrency() {
		Currency found = currency;
		if(found == null && currencyKey != null && Synapse.getInstance() != null) {
			found = Synapse.getInstance().getServiceProvider().find(EconomyService.class)
				.flatMap(eco -> eco.getCurrency(ResourceKey.tryParse(currencyKey)))
				.orElse(null);
			currency = found;
		}
		return found;
	}

	public String getCurrencyKey() {
		Currency found = currency;
		return found != null ? found.getKey().asString() : currencyKey;
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
