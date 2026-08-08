package sawfowl.synapse.implementapi.command.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.velocitypowered.api.command.CommandSource;

public class CommandArgument extends LiteralArgumentBuilder<CommandSource> {

	public CommandArgument(String literal) {
		super(literal);
	}

}
