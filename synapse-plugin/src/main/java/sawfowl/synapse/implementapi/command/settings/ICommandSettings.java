package sawfowl.synapse.implementapi.command.settings;

import java.util.Optional;

import sawfowl.synapse.api.commands.settings.CommandPrice;
import sawfowl.synapse.api.commands.settings.CommandSettings;

public class ICommandSettings implements CommandSettings {

	public static final ICommandSettings DEFAULT = new ICommandSettings();

	public static Builder builder() {
		return new ICommandSettings().createBuilder();
	}

	private Optional<CommandPrice> price;
	private int delay, cooldown;
	private ICommandSettings(){}

	@Override
	public Optional<CommandPrice> getPrice() {
		return price;
	}

	@Override
	public long getDelayExecute() {
		return delay;
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	private Builder createBuilder() {
		return new IBuilder();
	}

	private class IBuilder implements Builder {

		@Override
		public CommandSettings build() {
			return ICommandSettings.this;
		}

		@Override
		public Builder setPrice(CommandPrice price) {
			ICommandSettings.this.price = Optional.ofNullable(price);
			return this;
		}

		@Override
		public Builder setDelay(int delay) {
			ICommandSettings.this.delay = delay;
			return this;
		}

		@Override
		public Builder setCooldown(int cooldown) {
			ICommandSettings.this.cooldown = cooldown;
			return this;
		}

	}

}
