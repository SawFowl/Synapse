package sawfowl.synapse.commands;

import java.util.Locale;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.utils.TextUtils;

public class Tell extends AbstractCommand {

	@Override
	public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
		send(context.getSource(),
			command.<Player>getArgument(context, "Player").get(),
			command.<Component>getArgument(context, "Message").get());
		return command.success();
	}

	private void send(CommandSource source, Player player, Component message) throws CommandException {
		if(source instanceof Player sourcePlayer) {
			if(sourcePlayer.getUniqueId().equals(player.getUniqueId())) exception(getExceptions(source).getTargetSelf());
			source.sendMessage(getCommands(source).getTell().getSuccess(Component.text(player.getUsername()), message));
			player.sendMessage(getCommands(player).getTell().getSuccessTarget(Component.text(sourcePlayer.getUsername()), message).clickEvent(ClickEvent.suggestCommand("/proxytell " + sourcePlayer.getUsername() + " ")));
		} else {
			source.sendMessage(getCommands(Locale.getDefault()).getTell().getSuccess(Component.text(player.getUsername()), message));
			player.sendMessage(getCommands(player).getTell().getSuccessTarget(TextUtils.deserializeLegacy("&7[&4ProxyServer&7]"), message).clickEvent(ClickEvent.suggestCommand("/proxytell console ")));
		}
	}

	public static class Console extends AbstractCommand {

		@Override
		public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
			send((Player) context.getSource(), ((Player) context.getSource()).getEffectiveLocale(), command.<Component>getArgument(context, "Message").get());
			return command.success();
		}

		private void send(Player source, Locale sourceLocale, Component message) throws CommandException {
			source.sendMessage(getCommands(sourceLocale).getTell().getSuccess(TextUtils.deserializeLegacy("&7[&4ProxyServer&7]"), message));
			Synapse.getProxy().getConsoleCommandSource().sendMessage(getCommands(Locale.getDefault()).getTell().getSuccessTarget(TextUtils.deserializeLegacy(source.getUsername()), message));
		}

	}

}
