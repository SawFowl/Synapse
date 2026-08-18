package sawfowl.synapse.commands;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.exceptions.CommandException;

public class Broadcast extends AbstractCommand {

	@Override
	public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
		broadcast(command.<Component>getArgument(context, "Message").get(), new HashMap<>());
		return command.success();
	}

	private void broadcast(Component message, Map<Locale, Component> localized) {
		SynapsePlugin.getLogger().info(getCommands(Locale.getDefault()).getBroadcast(message));
		for(Player player : Synapse.getProxy().getAllPlayers()) {
			if(localized.containsKey(player.getEffectiveLocale())) {
				player.sendMessage(localized.get(player.getEffectiveLocale()));
				continue;
			}
			localized.put(player.getEffectiveLocale(), getCommands(player.getEffectiveLocale()).getBroadcast(message));
			player.sendMessage(localized.get(player.getEffectiveLocale()));
		}
		localized.clear();
	}

}
