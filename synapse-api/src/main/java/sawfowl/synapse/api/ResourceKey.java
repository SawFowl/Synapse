package sawfowl.synapse.api;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.builder.AbstractBuilder;

/**
 * ResourceKey is used to identify game objects with unique string identifiers.
 * 
 * @author SawFowl
 */
public interface ResourceKey {

	private static Builder builder() {
		return Synapse.getBuilderService().get(Builder.class);
	}

	static ResourceKey minecraft(String id) {
		return from("minecraft", id);
	}

	static ResourceKey from(@NotNull String namespace, @NotNull String id) {
		return builder().from(namespace, id);
	}

	static ResourceKey tryParse(@NotNull String string) throws RuntimeException {
		return builder().tryParse(string);
	}

	String getNamespace();

	String getId();

	default String asString() {
		return getNamespace() + ":" + getId();
	}

	default String asQuotedString() {
		return "\"" + asString() + "\"";
	}

	interface Builder extends AbstractBuilder<ResourceKey> {

		ResourceKey from(String namespace, String id);

		ResourceKey tryParse(String string);

	}

}
