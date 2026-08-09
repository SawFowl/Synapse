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
			return null;
		}

		@Override
		public ResourceKey from(String namespace, String id) {
			Objects.requireNonNull(namespace);
			Objects.requireNonNull(id);
			IResourceKey.this.namespace = namespace;
			IResourceKey.this.id = id;
			return IResourceKey.this;
		}

		@Override
		public ResourceKey tryParse(String string) throws RuntimeException {
			Objects.requireNonNull(string);
			if(string.contains("\"")) string = string.replace("\"", "");
			if(!string.contains(":")) return IResourceKey.this;
			var split = string.split(":");
			switch (split.length) {
				case 0: {
					split = null;
					return IResourceKey.this;
				}
				case 1: {
					namespace = split[0];
					split = null;
					return IResourceKey.this;
				}
				default: {
					namespace = split[0];
					id = split[1];
					split = null;
					return IResourceKey.this;
				}
			}
		}

	}

}
