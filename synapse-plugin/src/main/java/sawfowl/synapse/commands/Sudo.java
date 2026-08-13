package sawfowl.synapse.commands;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import sawfowl.synapse.Permissions;
import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand.ParameterizedExecutor;
import sawfowl.synapse.api.exceptions.CommandException;

public class Sudo implements ParameterizedExecutor {

	@Override
	public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
		Player player = command.<Player>getArgument(context, "Player").get();
		String spoofChatInput = command.getStringArgument(context, "SpoofChatInput").get();
		if(context.getSource() instanceof Player p && player.hasPermission(Permissions.SUDO_IGNORE)) exception(SynapsePlugin.getLocales().getAsReferenced(p).getCommands().getSudo().getFail(player));
		context.getSource().sendMessage(SynapsePlugin.getLocales().getAsReferenced(context.getSource()).getCommands().getSudo().getSuccess(player, spoofChatInput));
		player.spoofChatInput(spoofChatInput);
		return command.success();
	}

}
