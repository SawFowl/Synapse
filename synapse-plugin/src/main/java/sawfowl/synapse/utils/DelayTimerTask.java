package sawfowl.synapse.utils;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand.ParameterizedExecutor;
import sawfowl.synapse.api.economy.Currency;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.services.EconomyService;
import sawfowl.synapse.api.utils.ThrowingConsumer;
import sawfowl.synapse.implementapi.command.IBrigadierCommand;

public class DelayTimerTask implements Consumer<ScheduledTask> {


	private final UUID uuid;
	private long seconds;
	private final ThrowingConsumer<ParameterizedExecutor, CommandException> consumer;
	private long hour;
	private long minute;
	boolean first = true;
	final String command, ignoreEconomyPermission;
	private IBrigadierCommand commandClass;
	public DelayTimerTask(ThrowingConsumer<ParameterizedExecutor, CommandException> consumer, Player player, PluginContainer container, String command, IBrigadierCommand commandObject, String ignoreEconomyPermission) {
		this.uuid = player.getUniqueId();
		this.seconds = commandClass.getSettings().getDelayExecute();
		this.consumer = consumer;
		this.command = commandObject.getCommand();
		this.commandClass = commandObject;
		this.ignoreEconomyPermission = ignoreEconomyPermission;
	}

	@Override
	public void accept(ScheduledTask task) {
		if(seconds <= 0 || !getPlayer(uuid).isPresent() || !getPlayer(uuid).get().isActive()) {
			Synapse.getProxy().getScheduler().buildTask(this, () -> {
				getPlayer(uuid).ifPresent(player -> {
					try {
						economy(player);
						consumer.accept(commandClass.getExecutor());
					} catch (CommandException e) {
						player.sendMessage(e.componentMessage());
					}
				});
			}).schedule();
			task.cancel();
			return;
		} else {
			if(!getPlayer(uuid).isPresent() || !getPlayer(uuid).get().isActive()) {
				task.cancel();
				return;
			}
			Player player = getPlayer(uuid).get();
			if(getExpireHourFromNow(seconds) > 0) {
				if(hour != getExpireHourFromNow(seconds)) {
					hour = getExpireHourFromNow(seconds);
					player.sendMessage(SynapsePlugin.getLocales().getAsReferenced(player).getCommands().getWaitingForActivation(command, seconds, SynapsePlugin.getLocales().getAsReferenced(player).getTime()).hoverEvent(HoverEvent.showText(Component.text("/" + command).color(NamedTextColor.LIGHT_PURPLE))));
				}
			} else if(seconds > 60) {
				if(minute != getExpireMinuteFromNow(seconds)) {
					minute = getExpireMinuteFromNow(seconds);
					player.sendMessage(SynapsePlugin.getLocales().getAsReferenced(player).getCommands().getWaitingForActivation(command, seconds, SynapsePlugin.getLocales().getAsReferenced(player).getTime()).hoverEvent(HoverEvent.showText(Component.text("/" + command).color(NamedTextColor.LIGHT_PURPLE))));
				}
			} else if(seconds == 60 || seconds == 30 || seconds == 10 || seconds <= 5 || first) {
				first = false;
				player.sendMessage(SynapsePlugin.getLocales().getAsReferenced(player).getCommands().getWaitingForActivation(command, seconds, SynapsePlugin.getLocales().getAsReferenced(player).getTime()).hoverEvent(HoverEvent.showText(Component.text("/" + command).color(NamedTextColor.LIGHT_PURPLE))));
			}
			seconds--;
		}
	}

	void economy(Player player) throws CommandException {
		if(!Synapse.getInstance().getServiceProvider().isExist(EconomyService.class) || player.hasPermission(ignoreEconomyPermission)) return;
		var price = commandClass.getSettings().getPrice().orElse(null);
		if(price == null || price.getPrice().doubleValue() < 0) return;
		var currency = price.getCurrency();
		var account = Synapse.getInstance().getServiceProvider().get(EconomyService.class).getOrCreateUniqueAccount(player);
		if(!account.hasBalance(currency) || account.getBalance(currency).doubleValue() < price.getPrice().doubleValue()) exceptionMoney(player.getEffectiveLocale(), currency, price.getPrice());
	
	}

	private CommandException exceptionMoney(Locale locale, Currency currency, BigDecimal money) throws CommandException {
		return new CommandException(SynapsePlugin.getLocales().getAsReferenced(locale).getCommands().getExceptions().getNoMoney(currency, money, command));
	}

	private long getExpireHourFromNow(long second) {
		return TimeUnit.SECONDS.toHours(second);
	}

	private long getExpireMinuteFromNow(long second) {
		return TimeUnit.SECONDS.toMinutes(second);
	}

	private Optional<Player> getPlayer(UUID uuid) {
		return Synapse.getProxy().getPlayer(uuid);
	}

}
