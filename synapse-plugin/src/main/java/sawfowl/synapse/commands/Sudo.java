package sawfowl.synapse.commands;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import sawfowl.synapse.Permissions;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.exceptions.CommandException;

public class Sudo extends AbstractCommand {

	@Override
	public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
		Player player = command.<Player>getArgument(context, "Player").get();
		String spoofChatInput = command.getStringArgument(context, "SpoofChatInput").get();
		if(context.getSource() instanceof Player p && player.hasPermission(Permissions.SUDO_IGNORE)) exception(getCommands(p).getSudo().getFail(player));
		context.getSource().sendMessage(getCommands(context.getSource()).getSudo().getSuccess(player, spoofChatInput));
		player.spoofChatInput(spoofChatInput);
		return command.success();
	}

}
