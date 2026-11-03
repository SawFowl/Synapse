package sawfowl.synapse.implementapi;

import java.util.Objects;

import sawfowl.synapse.api.ResourceKey;

public class IResourceKey implements ResourceKey {

	public static Builder builder() {
		return new IResourceKey().createBuilder();
	}

	private String namespace = "unknown", id = "unknown";

	private Builder createBuilder() {
		return new IBuilder();
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return asString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, namespace);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(obj instanceof ResourceKey other) return equalsTo(other);
		return equalsTo(obj.toString());
	}

	private boolean equalsTo(ResourceKey other) {
		return Objects.equals(id, other.getId()) && Objects.equals(namespace, other.getNamespace());
	}

	private boolean equalsTo(String string) {
		if(string.contains("\"")) string = string.replace("\"", "");
		return Objects.equals(string, asString());
	}

	class IBuilder implements Builder {

		@Override
		public ResourceKey build() {
			// Used to return null.
			return IResourceKey.this;
		}

		@Override
		public ResourceKey from(String namespace, String id) {
			Objects.requireNonNull(namespace);
			Objects.requireNonNull(id);
			IResourceKey.this.namespace = namespace;
			IResourceKey.this.id = id;
			return IResourceKey.this;
		}

		/**
		 * {@code "namespace:id"}; a string without a namespace is a {@code minecraft} key, as in Adventure and Sponge.<br>
		 * The text was ignored without ':' (the key stayed {@code unknown:unknown}), and {@code "a:b:c"} lost {@code ":c"}.
		 */
		@Override
		public ResourceKey tryParse(String string) throws RuntimeException {
			Objects.requireNonNull(string);
			if(string.contains("\"")) string = string.replace("\"", "");
			int separator = string.indexOf(':');
			if(separator < 0) {
				if(!string.isEmpty()) {
					namespace = "minecraft";
					id = string;
				}
			} else {
				if(separator > 0) namespace = string.substring(0, separator);
				if(separator < string.length() - 1) id = string.substring(separator + 1);
			}
			return IResourceKey.this;
		}

	}

}
