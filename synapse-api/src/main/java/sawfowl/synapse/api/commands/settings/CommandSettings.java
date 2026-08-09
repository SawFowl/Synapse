package sawfowl.synapse.api.commands.settings;

import java.util.Optional;

import net.kyori.adventure.builder.AbstractBuilder;
import sawfowl.synapse.api.services.BuilderService;

public interface CommandSettings {

	static Builder builder() {
		return BuilderService.get().get(Builder.class);
	}

	Optional<CommandPrice> getPrice();

	long getDelayExecute();

	long getCooldown();

	interface Builder extends AbstractBuilder<CommandSettings> {

		Builder setPrice(CommandPrice price);

		Builder setDelay(int delay);

		Builder setCooldown(int cooldown);

	}

}
