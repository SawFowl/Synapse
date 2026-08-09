package sawfowl.synapse.api.commands.arguments;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;

@FunctionalInterface
public interface UsageSupplier {

	@NotNull Component get(CommandSource source);

}
