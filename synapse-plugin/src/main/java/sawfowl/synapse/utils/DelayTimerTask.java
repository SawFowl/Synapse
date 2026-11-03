package sawfowl.synapse.utils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.utils.ThrowingConsumer;
import sawfowl.synapse.implementapi.command.IBrigadierCommand;

public class DelayTimerTask implements Consumer<ScheduledTask> {

	private final UUID uuid;
	private long seconds;
	private final ThrowingConsumer<IBrigadierCommand, CommandException> consumer;
	private long hour;
	private long minute;
	private boolean first = true;
	private final String command;
	private IBrigadierCommand commandInstance;
	private static final Map<UUID, Set<ActiveExecutor>> EXECUTORS = new ConcurrentHashMap<>();
	private final UUID executorUUID;
	public DelayTimerTask(ThrowingConsumer<IBrigadierCommand, CommandException> consumer, Player player, PluginContainer container, String command, IBrigadierCommand commandObject) {
		this.uuid = player.getUniqueId();
		this.commandInstance = commandObject;
		this.consumer = consumer;
		this.command = command;
		this.seconds = commandInstance.getSettings().getDelay();
		executorUUID = UUID.randomUUID();
		EXECUTORS.computeIfAbsent(uuid, _ -> ConcurrentHashMap.newKeySet()).add(new ActiveExecutor(this.command, executorUUID));
	}

	public static boolean hasPending(Player player) {
		Set<ActiveExecutor> active = player == null ? null : EXECUTORS.get(player.getUniqueId());
		return active != null && !active.isEmpty();
	}

	public static boolean cancel(Player player, Component message) {
		if(player == null) return false;
		Set<ActiveExecutor> active = EXECUTORS.get(player.getUniqueId());
		if(active == null || active.isEmpty()) return false;
		active.forEach(executor -> {
			executor.stop = true;
			if(message != null) player.sendMessage(Text.of(message).replace("%command%", executor.command).get());
		});
		return true;
	}

	@Override
	public void accept(ScheduledTask task) {
		ActiveExecutor self = findSelf();
		if(self == null || self.stop) {
			removeSelf();
			task.cancel();
			return;
		}
		Optional<Player> optPlayer = getPlayer(uuid).filter(Player::isActive);
		if(seconds <= 0 || optPlayer.isEmpty()) {
			Synapse.getProxy().getScheduler().buildTask(SynapsePlugin.getInstance(), () -> {
				getPlayer(uuid).ifPresent(player -> {
					try {
						consumer.accept(commandInstance);
					} catch (CommandException e) {
						player.sendMessage(e.componentMessage());
					}
				});
			}).schedule();
			// Only this execution is finished: other delayed commands of the same player keep waiting.
			removeSelf();
			task.cancel();
			return;
		}
		Player player = optPlayer.get();
		if(getExpireHourFromNow(seconds) > 0) {
			if(hour != getExpireHourFromNow(seconds)) {
				hour = getExpireHourFromNow(seconds);
				sendWaiting(player);
			}
		} else if(seconds > 60) {
			if(minute != getExpireMinuteFromNow(seconds)) {
				minute = getExpireMinuteFromNow(seconds);
				sendWaiting(player);
			}
		} else if(seconds == 60 || seconds == 30 || seconds == 10 || seconds <= 5 || first) {
			first = false;
			sendWaiting(player);
		}
		seconds--;
	}

	private void sendWaiting(Player player) {
		var locale = SynapsePlugin.getLocales().getAsReferenced(player);
		player.sendMessage(locale.getCommands().getWaitingForActivation(command, seconds, locale.getTime()));
	}

	private ActiveExecutor findSelf() {
		Set<ActiveExecutor> active = EXECUTORS.get(uuid);
		if(active == null) return null;
		for(ActiveExecutor executor : active) if(executor.executorUUID.equals(executorUUID)) return executor;
		return null;
	}

	private void removeSelf() {
		EXECUTORS.computeIfPresent(uuid, (_, set) -> {
			set.removeIf(active -> active.executorUUID.equals(executorUUID));
			return set.isEmpty() ? null : set;
		});
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

	private class ActiveExecutor {
		ActiveExecutor(String command, UUID executorUUID) {
			this.command = command;
			this.executorUUID = executorUUID;
		}

		UUID executorUUID;
		String command;
		volatile boolean stop = false;

		@Override
		public int hashCode() {
			return 31 * Objects.hash(executorUUID);
		}
		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null || getClass() != obj.getClass()) return false;
			return Objects.equals(executorUUID, ((ActiveExecutor) obj).executorUUID);
		}

	}

}
