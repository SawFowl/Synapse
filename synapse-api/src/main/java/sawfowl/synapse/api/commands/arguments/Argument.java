package sawfowl.synapse.api.commands.arguments;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.CommandService;
import sawfowl.synapse.api.text.TextUtils;

public interface Argument<T> {

	static Argument<Player> PLAYER = CommandService.get().getArgument(null, "Player");

	static Argument<Duration> DURATION = CommandService.get().getArgument(null, "Duration");

	static Argument<Integer> createIntRange(String name, int min, int max) {
		return Argument.<Integer>builder().setName(name).setType(IntegerArgumentType.integer(min, max)).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Long> createLongRange(String name, long min, long max) {
		return Argument.<Long>builder().setName(name).setType(LongArgumentType.longArg(min, max)).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Float> createIntRange(String name, float min, float max) {
		return Argument.<Float>builder().setName(name).setType(FloatArgumentType.floatArg(min, max)).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Double> createDoubleRange(String name, double min, double max) {
		return Argument.<Double>builder().setName(name).setType(DoubleArgumentType.doubleArg(min, max)).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<String> createString(String name, String... variants) {
		return Argument.<String>builder()
			.setName(name)
			.setSuggestionProvider(
				(_, builder) -> {
					if(variants != null) for(String s : variants) builder.suggest(s);
					return builder.buildFuture();
				}
			)
			.setArgumentParser(arg -> Stream.of(variants).filter(var -> var.equals(arg.getResult())).findFirst())
			.build();
	}

	static Argument<Component> createString(String name) {
		return Argument.<Component>builder()
			.setName(name)
			.setArgumentParser(arg -> Optional.ofNullable(TextUtils.deserialize(arg.getResult().toString())))
			.build();
	}

	@SuppressWarnings("unchecked")
	static <E> Builder<E> builder() {
		return BuilderService.get().get(Builder.class);
	}

	String getName();

	<E extends T> Optional<E> parse(CommandContext<CommandSource> context);

	interface Builder<T> extends AbstractBuilder<Argument<T>> {

		Builder<T> setName(String name);

		Builder<T> setRequirement(Predicate<CommandSource> requirement);

		Builder<T> setSuggestionProvider(SuggestionProvider<CommandSource> suggestionsProvider);

		Builder<T> setArgumentParser(ArgumentParser<CommandSource, T> parser);

		/**
		 * default {@link StringArgumentType#word}
		 */
		Builder<T> setType(ArgumentType<T> type);

	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<T> cast(Object object) {
		try {
			return (Optional<T>) Optional.ofNullable(object);
		} catch (Exception e) {
			return null;
		}
	}

}
