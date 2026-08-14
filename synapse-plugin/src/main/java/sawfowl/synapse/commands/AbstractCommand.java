package sawfowl.synapse.commands;

import java.util.Locale;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand.ParameterizedExecutor;
import sawfowl.synapse.configure.localization.Time;
import sawfowl.synapse.configure.localization.commands.Commands;
import sawfowl.synapse.configure.localization.commands.Exceptions;

public abstract class AbstractCommand implements ParameterizedExecutor {

	protected Commands getCommands(Locale locale) {
		return SynapsePlugin.getLocales().getAsReferenced(locale).getCommands();
	}

	protected Commands getCommands(Player player) {
		return SynapsePlugin.getLocales().getAsReferenced(player).getCommands();
	}

	protected Commands getCommands(CommandSource commandSource) {
		return SynapsePlugin.getLocales().getAsReferenced(commandSource).getCommands();
	}

	protected Exceptions getExceptions(Locale locale) {
		return getCommands(locale).getExceptions();
	}

	protected Exceptions getExceptions(Player player) {
		return getCommands(player).getExceptions();
	}

	protected Exceptions getExceptions(CommandSource commandSource) {
		return getCommands(commandSource).getExceptions();
	}

	protected Time getTime(Locale locale) {
		return SynapsePlugin.getLocales().getAsReferenced(locale).getTime();
	}

}
