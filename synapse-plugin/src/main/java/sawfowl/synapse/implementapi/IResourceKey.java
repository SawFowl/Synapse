package sawfowl.synapse.implementapi;

import java.util.Objects;

import sawfowl.synapse.api.ResourceKey;

public class IResourceKey implements ResourceKey {

	public static Builder builder() {
		return new IResourceKey().createBuilder();
	}

	private String namespace, id;

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
		if(obj == null || getClass() != obj.getClass()) return false;
		return equalsTo((IResourceKey) obj);
	}

	private boolean equalsTo(IResourceKey other) {
		return Objects.equals(id, other.id) && Objects.equals(namespace, other.namespace);
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
			if(!string.contains(":")) throw new RuntimeException("The string '" + string + "' does not contain the character ':'.");
			var split = string.split(":");
			if(split.length < 2 || split[0].length() == 0 || split[1].length() == 0 || split[1].contains(":")) throw new RuntimeException("It is impossible to parse string '" + string + "' correctly. The character ':' must not be the first or last character. It is also unacceptable to have multiple characters ':'");
			namespace = split[0];
			id = split[1];
			split = null;
			return IResourceKey.this;
		}
		
	}

}
