package sawfowl.synapse.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
	private static final Map<UUID, Set<ActiveExecutor>> EXECUTORS = new HashMap<>();
	private final UUID executorUUID;
	public DelayTimerTask(ThrowingConsumer<IBrigadierCommand, CommandException> consumer, Player player, PluginContainer container, String command, IBrigadierCommand commandObject) {
		this.uuid = player.getUniqueId();
		this.commandInstance = commandObject;
		this.consumer = consumer;
		this.command = command;
		this.seconds = commandInstance.getSettings().getDelay();
		if(!EXECUTORS.containsKey(uuid)) EXECUTORS.put(uuid, new HashSet<>());
		executorUUID = UUID.randomUUID();
		EXECUTORS.get(uuid).add(new ActiveExecutor(this.command, executorUUID));
	}

	public static boolean cancel(Player player, Component message) {
		if(player != null && EXECUTORS.containsKey(player.getUniqueId())) {
			EXECUTORS.get(player.getUniqueId()).forEach(active -> {
				active.stop = true;
				if(message != null) player.sendMessage(Text.of(message).replace("%command%", active.command).get());
			});
			return true;
		}
		return false;
	}

	@Override
	public void accept(ScheduledTask task) {
		if(!EXECUTORS.containsKey(uuid)) {
			task.cancel();
			return;
		} else if(EXECUTORS.get(uuid).stream().filter(active -> active.executorUUID.equals(executorUUID)).map(active -> active.stop).findFirst().orElse(false)) {
			EXECUTORS.get(uuid).removeIf(active -> active.executorUUID.equals(executorUUID));
			task.cancel();
			return;
		} else if(!EXECUTORS.get(uuid).stream().filter(active -> active.executorUUID.equals(executorUUID)).findFirst().isPresent()) {
			task.cancel();
			return;
		}
		if(seconds <= 0 || !getPlayer(uuid).isPresent() || !getPlayer(uuid).get().isActive()) {
			Synapse.getProxy().getScheduler().buildTask(SynapsePlugin.getInstance(), () -> {
				getPlayer(uuid).ifPresent(player -> {
					try {
						consumer.accept(commandInstance);
					} catch (CommandException e) {
						player.sendMessage(e.componentMessage());
					}
				});
			}).schedule();
			EXECUTORS.remove(uuid);
			task.cancel();
			return;
		} else {
			if(!getPlayer(uuid).isPresent() || !getPlayer(uuid).get().isActive()) {
				EXECUTORS.remove(uuid);
				task.cancel();
				return;
			}
			Player player = getPlayer(uuid).get();
			if(getExpireHourFromNow(seconds) > 0) {
				if(hour != getExpireHourFromNow(seconds)) {
					hour = getExpireHourFromNow(seconds);
					player.sendMessage(SynapsePlugin.getLocales().getAsReferenced(player).getCommands().getWaitingForActivation(command, seconds, SynapsePlugin.getLocales().getAsReferenced(player).getTime()));
				}
			} else if(seconds > 60) {
				if(minute != getExpireMinuteFromNow(seconds)) {
					minute = getExpireMinuteFromNow(seconds);
					player.sendMessage(SynapsePlugin.getLocales().getAsReferenced(player).getCommands().getWaitingForActivation(command, seconds, SynapsePlugin.getLocales().getAsReferenced(player).getTime()));
				}
			} else if(seconds == 60 || seconds == 30 || seconds == 10 || seconds <= 5 || first) {
				first = false;
				player.sendMessage(SynapsePlugin.getLocales().getAsReferenced(player).getCommands().getWaitingForActivation(command, seconds, SynapsePlugin.getLocales().getAsReferenced(player).getTime()));
			}
			seconds--;
		}
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
		boolean stop = false;

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
