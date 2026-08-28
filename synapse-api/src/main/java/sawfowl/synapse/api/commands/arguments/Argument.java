package sawfowl.synapse.api.commands.arguments;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.ResourceKey;
import sawfowl.synapse.api.services.BuilderService;
import sawfowl.synapse.api.services.CommandService;
import sawfowl.synapse.api.utils.StringUtils;
import sawfowl.synapse.api.utils.TextUtils;

/**
 * The interface is designed for simplified creation of command arguments.<br>
 * When using this interface, you won’t need to write identical code multiple times for different commands to process an argument.<br>
 * Create an argument with the type you need and the corresponding parser;<br>
 * after that, you can assign this argument to any commands.<br>
 * Synapse will make a copy of your settings for each of your commands to which you assign the argument.<br>
 * Creating copies is necessary because each of them is assigned its own command executor.
 * 
 * @author SawFowl
 */
public interface Argument<T> {

	static String[] EMPTY_VARIANTS = {};

	static String[] BOOLEAN_VARIANTS = {"true", "false"};

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
				.setVariants(false, _ -> BOOLEAN_VARIANTS)
				.build();
	}

	static Argument<String> createString(String name, boolean optional, boolean allowAny, String... variants) {
		return Argument.<String>builder()
			.setName(name)
			.setArgumentParser(arg -> allowAny || variants == null || variants.length == 0 ? Optional.ofNullable(arg.getResult().toString()) : Stream.of(variants).filter(var -> var.equals(arg.getResult())).findFirst())
			.setOptional(optional)
			.setVariants(allowAny, _ -> variants == null || variants.length == 0 ? EMPTY_VARIANTS : variants)
			.build();
	}

	static Argument<ResourceKey> createResourceKey(String name, boolean optional, boolean allowAny, ResourceKey... variants) {
		return Argument.<ResourceKey>builder()
			.setName(name)
			.setArgumentParser(arg -> (allowAny && arg.getResult() instanceof String s && !s.startsWith(":") && !s.endsWith(":") && StringUtils.countMatches(s, ':') == 1) ||  variants == null || variants.length == 0 ? Optional.ofNullable(ResourceKey.tryParse(arg.getResult().toString())) : Stream.of(variants).filter(var -> var.asString().equals(arg.getResult().toString())).findFirst())
			.setOptional(optional)
			.setType(StringArgumentType.string())
			.setVariants(allowAny, _ -> variants == null || variants.length == 0 ? EMPTY_VARIANTS : Stream.of(variants).map(ResourceKey::asQuotedString).toArray(String[]::new))
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

	/**
	 * See {@link ArgumentParser}
	 */
	<E extends T> Optional<E> parse(CommandContext<CommandSource> context);

	boolean isOptional();

	/**
	 * It will return `null` until the command containing this argument is built.
	 */
	@Nullable CommandNode<CommandSource> asCommandNode();

	interface Builder<T> extends AbstractBuilder<Argument<T>> {

		/**
		 * Assigning an argument name. The name of each command argument must be unique.
		 */
		Builder<T> setName(String name);

		/**
		 * You can set the requirements for the permissibility of the argument input.
		 */
		Builder<T> setRequirement(Predicate<CommandSource> requirement);

		/**
		 * @param parser - See {@link ArgumentParser}
		 */
		Builder<T> setArgumentParser(ArgumentParser<CommandSource, T> parser);

		/**
		 * 
		 * @param predicate - See {@link ArgumentParser.Predicate}
		 * @param parser - See {@link ArgumentParser}
		 * @return
		 */
		Builder<T> setArgumentParser(ArgumentParser.Predicate predicate, ArgumentParser<CommandSource, T> parser);

		/**
		 * Default {@link StringArgumentType#word}<br>
		 * Do not try to create your own argument types. This is impossible without modifying the client.
		 */
		Builder<T> setType(ArgumentType<?> type);

		/**
		 * Should the argument be optional?
		 */
		Builder<T> setOptional(boolean value);

		/**
		 * @param allowAny - If it is false, then only input that matches any of the pre‑defined options will be considered valid.
		 * @param variants - See {@link ArgumentSupplier}
		 * @return
		 */
		Builder<T> setVariants(boolean allowAny, ArgumentSupplier variants);

		/**
		 * @param supplier - See {@link UsageSupplier}
		 */
		Builder<T> setUsage(UsageSupplier supplier);

	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<T> cast(Object object) {
		try {
			return Optional.ofNullable((T) object);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

}
