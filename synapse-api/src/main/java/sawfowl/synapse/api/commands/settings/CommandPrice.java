package sawfowl.synapse.api.commands.settings;

import java.math.BigDecimal;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.builder.AbstractBuilder;

import sawfowl.synapse.api.economy.Currency;
import sawfowl.synapse.api.services.BuilderService;

public interface CommandPrice {

	private static Builder builder() {
		return BuilderService.get().get(Builder.class);
	}

	static CommandPrice of(@NotNull Currency currency, @NotNull BigDecimal price) {
		return builder().of(currency, price);
	}

	static CommandPrice of(@NotNull Currency currency, double price) {
		return of(currency, BigDecimal.valueOf(price));
	}

	Currency getCurrency();

	BigDecimal getPrice();

	interface Builder extends AbstractBuilder<CommandPrice> {

		CommandPrice of(Currency currency, BigDecimal price);

	}

}
