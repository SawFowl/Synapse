package sawfowl.synapse.implementapi.command.argument;


import java.util.function.Predicate;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand.ParameterizedExecutor;

public class SimpleCommandArgument extends AbstractArgumentBuilder {

	private Predicate<CommandSource> canUse = _ -> true;
	public SimpleCommandArgument(SynapseBrigadierCommand command, String literal, Predicate<CommandSource> canUse, ParameterizedExecutor executor) {
		super(command, literal, executor);
	}

	protected Predicate<CommandSource> canUse() {
		return canUse;
	}

	@Override
	protected Component сanNotBeUsed(CommandSource commandSource) {
		return Component.text("Вы не можете использовать эту команду.");
	}

}
