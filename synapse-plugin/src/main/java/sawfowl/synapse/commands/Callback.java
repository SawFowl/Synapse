package sawfowl.synapse.commands;

import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.services.CallbackSevice;

public class Callback extends AbstractCommand {

	@Override
	public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
		run(context.getSource(), CallbackSevice.get().getCallback(command.getStringArgument(context, "CallbackId").get(), context.getSource()));
		return command.success();
	}

	private void run(CommandSource source, Optional<Consumer<CommandSource>> optCallback) throws CommandException {
		if(!optCallback.isPresent()) exception(getExceptions(source).getCallAfterRemoval());
		optCallback.get().accept(source);
	}

	public static class Pagination extends AbstractCommand {

		public Pagination() {}

		@Override
		public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
			run(context.getSource(), CallbackSevice.get().getPagination(command.getStringArgument(context, "Page").get(), context.getSource()));
			return command.success();
		}

		private void run(CommandSource source, Optional<Consumer<CommandSource>> optCallback) throws CommandException {
			if(!optCallback.isPresent()) exception(getExceptions(source).getPageNotExist());
			optCallback.get().accept(source);
		}

	}

}
