package sawfowl.synapse.commands;

import java.util.Optional;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import sawfowl.synapse.Permissions;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.implementapi.command.argument.GenericArgumentBuilder;

public class Server extends AbstractCommand {

	public static final Argument<?>[] ARGUMENTS = {
		GenericArgumentBuilder.<RegisteredServer>builder()
			.setName("Server")
			.setArgumentParser(
				(_, context) -> 
				context.getArguments().containsKey("Server") && Synapse.getProxy().getAllServers().stream().filter(s -> s.getServerInfo().getName().equals(context.getArguments().get("Server").getResult().toString()) && context.getSource().hasPermission(Permissions.getServerArg(s.getServerInfo().getName()))).findFirst().isPresent(),
				arg -> Synapse.getProxy().getAllServers().stream().filter(s -> s.getServerInfo().getName().equals(arg.getResult().toString())).findFirst()
			)
			.setVariants(false, context -> Synapse.getProxy().getAllServers().stream().map(s -> s.getServerInfo().getName()).filter(s -> context.getSource().hasPermission(Permissions.getServerArg(s))).toArray(String[]::new))
			.build(),
		GenericArgumentBuilder.<Player>builder()
			.setName("Player")
			.setOptional(true)
			.setRequirement(source -> source.hasPermission(Permissions.SERVER_ADMIN))
			.setArgumentParser(arg -> Synapse.getProxy().getAllPlayers().stream().filter(p -> p.getUsername().equals(arg.getResult().toString())).findFirst())
			.setVariants(false, _ -> Synapse.getProxy().getAllPlayers().stream().map(Player::getUsername).toArray(String[]::new))
			.build()
	};

	@Override
	public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
		run(context, command.<RegisteredServer>getArgument(context, "Server").get(), command.getArgument(context, "Player"));
		return command.success();
	}

	private void run(CommandContext<CommandSource> context, RegisteredServer server, Optional<Player> player) throws CommandException {
		if(player.isPresent()) {
			player.get().createConnectionRequest(server);
			player.get().sendMessage(getCommands(player.get()).getServer().getSuccess(server.getServerInfo().getName()));
			context.getSource().sendMessage(getCommands(context.getSource()).getServer().getSuccessTarget(server.getServerInfo().getName(), player.get().getUsername()));
		} else if(context.getSource() instanceof Player source) {
			source.createConnectionRequest(server);
			source.sendMessage(getCommands(source).getServer().getSuccess(server.getServerInfo().getName()));
		} else exception(getCommands(context.getSource()).getExceptions().getPlayerNotPresent());
	}

}
