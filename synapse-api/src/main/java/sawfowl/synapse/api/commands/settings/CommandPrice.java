package sawfowl.synapse.api.commands.settings;

import java.math.BigDecimal;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.builder.AbstractBuilder;

import sawfowl.synapse.api.economy.Currency;
import sawfowl.synapse.api.services.BuilderService;

/**
 * Setting the command execution price.<br>
 * These settings are used only if the player executes the command.
 * 
 * @author SawFowl
 */
public interface CommandPrice {

	private static Builder builder() {
		return BuilderService.get().get(Builder.class);
	}

	static CommandPrice of(@NotNull Currency currency, @NotNull BigDecimal price, String ignorePermission) {
		return builder().of(currency, price, ignorePermission);
	}

	static CommandPrice of(@NotNull Currency currency, double price, String ignorePermission) {
		return of(currency, BigDecimal.valueOf(price), ignorePermission);
	}

	Currency getCurrency();

	BigDecimal getPrice();

	String getIgnorePermission();

	interface Builder extends AbstractBuilder<CommandPrice> {

		CommandPrice of(Currency currency, BigDecimal price, String ignorePermission);

	}

}
