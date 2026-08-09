package sawfowl.synapse.api.commands.arguments;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.ResourceKey;
import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.CommandService;
import sawfowl.synapse.api.utils.TextUtils;

public interface Argument<T> {

	static Argument<Player> PLAYER = CommandService.get().getArgument("Player", false);

	static Argument<RegisteredServer> SERVER = CommandService.get().getArgument("Server", false);

	static Argument<Duration> DURATION = CommandService.get().getArgument("Duration", false);

	static Argument<Player> OPTIONAL_PLAYER = CommandService.get().getArgument("Player", true);

	static Argument<RegisteredServer> OPTIONAL_SERVER = CommandService.get().getArgument("Server", true);

	static Argument<Duration> OPTIONAL_DURATION = CommandService.get().getArgument("Duration", true);

	static Argument<Integer> createInt(String name, boolean optional) {
		return Argument.<Integer>builder().setName(name).setType(IntegerArgumentType.integer()).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Long> createLong(String name, boolean optional) {
		return Argument.<Long>builder().setName(name).setType(LongArgumentType.longArg()).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Float> createFloat(String name, boolean optional) {
		return Argument.<Float>builder().setName(name).setType(FloatArgumentType.floatArg()).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Double> createDouble(String name, boolean optional) {
		return Argument.<Double>builder().setName(name).setType(DoubleArgumentType.doubleArg()).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Integer> createIntRange(String name, boolean optional, int min, int max) {
		return Argument.<Integer>builder().setName(name).setType(IntegerArgumentType.integer(min, max)).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Long> createLongRange(String name, boolean optional, long min, long max) {
		return Argument.<Long>builder().setName(name).setType(LongArgumentType.longArg(min, max)).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Float> createFloatRange(String name, boolean optional, float min, float max) {
		return Argument.<Float>builder().setName(name).setType(FloatArgumentType.floatArg(min, max)).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Double> createDoubleRange(String name, boolean optional, double min, double max) {
		return Argument.<Double>builder().setName(name).setType(DoubleArgumentType.doubleArg(min, max)).setOptional(optional).setArgumentParser(arg -> cast(arg.getResult())).build();
	}

	static Argument<Boolean> createBoolean(String name, boolean optional) {
		return Argument.<Boolean>builder()
				.setName(name)
				.setType(BoolArgumentType.bool())
				.setArgumentParser(arg -> cast(arg.getResult()))
				.setVariants(_ -> new String[]{"true", "false"})
				.build();
	}

	static Argument<String> createString(String name, boolean optional, String... variants) {
		return Argument.<String>builder()
			.setName(name)
			.setArgumentParser(arg -> Stream.of(variants).filter(var -> var.equals(arg.getResult())).findFirst())
			.setOptional(optional)
			.setVariants(_ -> variants)
			.build();
	}

	static Argument<ResourceKey> createResourceKey(String name, boolean optional, ResourceKey... variants) {
		return Argument.<ResourceKey>builder()
			.setName(name)
			.setArgumentParser(arg -> Stream.of(variants).filter(var -> var.equals(arg.getResult())).findFirst())
			.setOptional(optional)
			.setType(StringArgumentType.string())
			.setVariants(_ -> Stream.of(variants).map(ResourceKey::asQuotedString).toArray(String[]::new))
			.build();
	}

	/**
	 * This argument is added to the command only last!
	 */
	static Argument<String> createGreedyString(String name, boolean optional) {
		return Argument.<String>builder()
			.setName(name)
			.setArgumentParser(arg -> Optional.ofNullable(arg.getResult().toString()))
			.setType(StringArgumentType.greedyString())
			.setOptional(optional)
			.build();
	}

	/**
	 * This argument is added to the command only last!
	 */
	static Argument<Component> createComponent(String name, boolean optional) {
		return Argument.<Component>builder()
			.setName(name)
			.setArgumentParser(arg -> Optional.ofNullable(TextUtils.deserialize(arg.getResult().toString())))
			.setType(StringArgumentType.greedyString())
			.setOptional(optional)
			.build();
	}

	@SuppressWarnings("unchecked")
	static <E> Builder<E> builder() {
		return BuilderService.get().get(Builder.class);
	}

	String getName();

	<E extends T> Optional<E> parse(CommandContext<CommandSource> context);

	boolean isOptional();

	interface Builder<T> extends AbstractBuilder<Argument<T>> {

		Builder<T> setName(String name);

		Builder<T> setRequirement(Predicate<CommandSource> requirement);

		Builder<T> setArgumentParser(ArgumentParser<CommandSource, T> parser);

		/**
		 * default {@link StringArgumentType#word}
		 */
		Builder<T> setType(ArgumentType<?> type);

		Builder<T> setOptional(boolean value);

		Builder<T> setVariants(ArgumentSupplier variants);

		Builder<T> setUsage(UsageSupplier supplier);

	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<T> cast(Object object) {
		try {
			return Optional.ofNullable((T) object);
		} catch (Exception e) {
			return null;
		}
	}

}
