package sawfowl.synapse.api.economy;

import java.math.BigDecimal;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.ResourceKey;

/**
 * By default, Synapse does not contain an implementation of this interface.<br>
 * It is assumed that the economy API will be implemented by another plugin to ensure synchronization with game servers.
 * 
 * @author SawFowl
 */
public interface Currency {

	/**
	 * Preferably used to compare currencies.<br>
	 * Each currency must have a unique identifier.
	 * 
	 * @return The currency identifier.
	 */
	ResourceKey getKey();

	/**
	 * The currency's display name, in singular form. Ex: Dollar.
	 */
	Component getDisplayName();

	/**
	 * The currency's display name in plural form. Ex: Dollars.
	 */
	Component getPluralDisplayName();

	/**
	 * The currency's symbol. Ex. $
	 */
	char getChar();

	/**
	 * The default balance for this currency.
	 */
	BigDecimal getDefaultBalance();

	/**
	 * Returns true if this currency is the default currency for the economy,
	 * otherwise false.
	 * 
	 * @return true if this is the default currency
	 */
	boolean isDefault();

	default Component getStyledChar() {
		return Component.text(getChar()).style(getDisplayName().style()).color(getDisplayName().color());
	}

}
