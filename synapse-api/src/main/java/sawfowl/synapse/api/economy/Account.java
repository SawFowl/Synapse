package sawfowl.synapse.api.economy;

import java.math.BigDecimal;

import sawfowl.synapse.api.services.EconomyService;

public interface Account {

	String getName();

	boolean hasBalance(Currency currency);

	BigDecimal getBalance(Currency currency);

	void set(Currency currency, BigDecimal balance);

	boolean add(Currency currency, BigDecimal money);

	boolean withdraw(Currency currency, BigDecimal money);

	default <A extends Account> boolean transferFrom(A account, Currency currency, BigDecimal money) {
		return account.transferTo(this, currency, money);
	}

	default <A extends Account> boolean transferTo(A account, Currency currency, BigDecimal money) {
		if(!hasBalance(currency) || getBalance(currency).doubleValue() - money.doubleValue() < 0) return false;
		return withdraw(currency, money) && account.add(currency, money);
	}

	default void setDefaultBalances() {
		EconomyService.get().getCurrencies().forEach(this::setDefaultBalance);
	}

	default void setDefaultBalance() {
		setDefaultBalance(EconomyService.get().getDefaultCurrency());
	}

	default void setDefaultBalance(Currency currency) {
		set(currency, currency.getDefaultBalance());
	}

}
