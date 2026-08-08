package sawfowl.synapse.implementapi.command.argument;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.command.CommandSource;

import sawfowl.synapse.api.commands.arguments.Argument;
import sawfowl.synapse.api.commands.arguments.ArgumentParser;

public class GenericArgumentBuilder<S extends CommandSource, A extends GenericArgumentBuilder<S, A, T>, T> extends ArgumentBuilder<S, A> implements Argument<T> {

	@SuppressWarnings("unchecked")
	public static <T> Builder<T> builder() {
		return (Builder<T>) new GenericArgumentBuilder<>().createBuilder();
	}

	@SuppressWarnings("unchecked")
	private GenericArgumentBuilder() {
		this.type = (ArgumentType<T>) StringArgumentType.word();
		requirement = _ -> true;
		suggestionsProvider = (_, builder) -> builder.buildFuture();
		parser = (_) -> null;
	}

	private String name;
	private ArgumentType<T> type;
	private Command<S> command;
	private Predicate<S> requirement;
	private SuggestionProvider<S> suggestionsProvider;
	private ArgumentParser<S, T> parser;
	private boolean optional = false;
	private Supplier<T[]> variants;
	private GenericArgumentBuilder(String name, ArgumentType<T> type, Predicate<S> requirement, SuggestionProvider<S> suggestionsProvider, ArgumentParser<S, T> parser) {
		this.name = name;
		this.type = type;
		this.requirement = requirement;
		this.suggestionsProvider = suggestionsProvider;
		this.parser = parser;
	}

	public GenericArgumentBuilder<S, A, T> copy() {
		return new GenericArgumentBuilder<>(name, type, requirement, suggestionsProvider, parser);
	}

	@Override
	public String getName() {
		return name;
	}

	public GenericArgumentBuilder<S, A, T> setCommand(Command<S> command) {
		this.command = command;
		return this;
	}

	@Override
	public CommandNode<S> build() {
		final ArgumentCommandNode<S, ?> result = new ArgumentCommandNode<>(name, type, command, requirement, getRedirect(), getRedirectModifier(), isFork(), suggestionsProvider);
		for (final CommandNode<S> argument : getArguments()) {
			result.addChild(argument);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected A getThis() {
		return (A) this;
	}

	@SuppressWarnings("unchecked")
	public <E extends T> Optional<E> parse(CommandContext<CommandSource> context) {
		return (Optional<E>) parseArg((CommandContext<S>) context);
	}

	public Optional<T> parseArg(CommandContext<S> context) {
		return context.getArguments().containsKey(name) ? parseArg(context.getArguments().get(name)) : Optional.empty();
	}

	public ArgumentParser<S, T> getParser() {
		return parser;
	}

	private Optional<T> parseArg(ParsedArgument<S, ?> arg) {
		return parser.parse(arg);
	}

	private Builder<T> createBuilder() {
		return new IBuilder();
	}

	@Override
	public boolean isOprional() {
		return optional;
	}

	public T[] getVariants() {
		return variants != null ? variants.get() : null;
	}

	private class IBuilder implements Builder<T> {

		@Override
		public Argument<T> build() {
			return GenericArgumentBuilder.this;
		}

		@Override
		public Builder<T> setName(String name) {
			Objects.requireNonNull(name);
			GenericArgumentBuilder.this.name = name;
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Builder<T> setRequirement(Predicate<CommandSource> requirement) {
			Objects.requireNonNull(requirement);
			GenericArgumentBuilder.this.requirement = (Predicate<S>) requirement;
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Builder<T> setSuggestionProvider(SuggestionProvider<CommandSource> suggestionsProvider) {
			Objects.requireNonNull(suggestionsProvider);
			GenericArgumentBuilder.this.suggestionsProvider = (SuggestionProvider<S>) suggestionsProvider;
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Builder<T> setArgumentParser(ArgumentParser<CommandSource, T> parser) {
			Objects.requireNonNull(parser);
			GenericArgumentBuilder.this.parser = (ArgumentParser<S, T>) parser;
			return this;
		}

		@Override
		public Builder<T> setType(ArgumentType<T> type) {
			Objects.requireNonNull(type);
			GenericArgumentBuilder.this.type = type;
			return this;
		}

		@Override
		public Builder<T> setOptional(boolean value) {
			optional = value;
			return this;
		}

		@Override
		public Builder<T> setVariants(Supplier<T[]> variants) {
			GenericArgumentBuilder.this.variants = variants;
			return this;
		}

	}

}
