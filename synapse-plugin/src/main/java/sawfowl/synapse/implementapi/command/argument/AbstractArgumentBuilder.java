package sawfowl.synapse.implementapi.command.argument;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand.ParameterizedExecutor;

public abstract class AbstractArgumentBuilder extends LiteralArgumentBuilder<CommandSource> {

	public AbstractArgumentBuilder(SynapseBrigadierCommand command, String literal, ParameterizedExecutor executor) {
		super(literal);
		this.executes(context -> {
			executor.execute(command, context);
			return success();
		});
	}

	protected abstract Predicate<CommandSource> canUse();

	protected abstract Component сanNotBeUsed(CommandSource source);

	protected int success() {
		return Command.SINGLE_SUCCESS;
	}

}
