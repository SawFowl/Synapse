package sawfowl.synapse.api.services;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.velocitypowered.api.proxy.Player;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.economy.Account;
import sawfowl.synapse.api.economy.Currency;
import sawfowl.synapse.api.economy.UniqueAccount;

public interface EconomyService {

	static EconomyService get() {
		return Synapse.getInstance().getServiceProvider().get(EconomyService.class);
	}

	Currency getDefaultCurrency();

	Set<Currency> getCurrencies();

	Optional<Account> findAccount(String name);

	Optional<UniqueAccount> findUniqueAccount(UUID uuid);

	Account getOrCreateAccount(String name);

	UniqueAccount getOrCreateUniqueAccount(UUID uuid, String name);

	default UniqueAccount getOrCreateUniqueAccount(Player player) {
		return getOrCreateUniqueAccount(player.getGameProfile().getId(), player.getGameProfile().getName());
	}

}
