package sawfowl.synapse.implementapi.text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.utils.TextUtils;

public class IText implements Text {

	public static Builder builder() {
		return new IText().createBuilder();
	}

	private Component component = Component.empty();
	public IText() {}

	@Override
	public Component get() {
		return component;
	}

	@Override
	public String toPlain() {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	@Override
	public Text append(Component component) {
		// Component is immutable: the result has to be stored, otherwise append() does nothing.
		this.component = this.component.append(component);
		return this;
	}

	@Override
	public Text append(Text text) {
		return append(text.get());
	}

	@Override
	public Text replace(String key, String value) {
		component = component.replaceText(TextReplacementConfig.builder().matchLiteral(key).replacement(value == null ? "" : value).build());
		return this;
	}

	@Override
	public Text replace(String key, Component value) {
		component = component.replaceText(TextReplacementConfig.builder().matchLiteral(key).replacement(value).build());
		return this;
	}

	@Override
	public Text replace(String key, Text value) {
		component = component.replaceText(TextReplacementConfig.builder().matchLiteral(key).replacement(value.get()).build());
		return this;
	}

	@Override
	public Text replace(String key, Object value) {
		// null (e.g. a client brand that has not arrived yet) becomes empty text instead of an NPE.
		return replace(key, value == null ? "" : value.toString());
	}

	@Override
	public Text replace(String[] keys, String... values) {
		replace(replaceMap(keys, values));
		return this;
	}

	@Override
	public Text replace(String[] keys, Object... values) {
		replace(replaceMap(keys, values));
		return this;
	}

	@Override
	public Text replace(String[] keys, Component... values) {
		replaceComponents(replaceMapComponents(keys, values));
		return this;
	}

	@Override
	public Text replace(String[] keys, Text... values) {
		replaceComponents(replaceMapTexts(keys, values));
		return this;
	}

	@Override
	public Text createCallBack(Consumer<CommandSource> callback) {
		component = TextUtils.createCallBack(component, callback);
		return this;
	}

	@Override
	public Text createCallBack(Runnable runnable) {
		component = TextUtils.createCallBack(component, runnable);
		return this;
	}

	@Override
	public Text removeDecorations() {
		component = TextUtils.removeDecorations(component);
		return this;
	}

	private void replace(Map<String, String> map) {
		map.forEach((k, v) -> replace(k, v));
	}

	private void replaceComponents(Map<String, Component> map) {
		map.forEach((k, v) -> replace(k, v));
	}

	private Map<String, String> replaceMap(String[] keys, Object[] values) {
		return IntStream.range(0, keys.length).boxed().collect(Collectors.toMap(i -> keys[i], i -> values.length > i && values[i] != null ? values[i].toString() : "", (first, _) -> first, LinkedHashMap::new));
	}

	private Map<String, Component> replaceMapComponents(String[] keys, Component[] values) {
		return IntStream.range(0, keys.length).boxed().collect(Collectors.toMap(i -> keys[i], i -> values.length > i && values[i] != null ? values[i] : Component.empty(), (first, _) -> first, LinkedHashMap::new));
	}

	private Map<String, Component> replaceMapTexts(String[] keys, Text[] values) {
		return IntStream.range(0, keys.length).boxed().collect(Collectors.toMap(i -> keys[i], i -> values.length > i && values[i] != null ? values[i].get() : Component.empty(), (first, _) -> first, LinkedHashMap::new));
	}

	private Builder createBuilder() {
		return new IBuilder();
	}

	public class IBuilder implements Builder {

		@Override
		public Text build() {
			return IText.this;
		}

		@Override
		public Text fromComponent(Component component) {
			IText.this.component = component;
			return IText.this;
		}

		@Override
		public Text fromString(String string) {
			component = TextUtils.deserialize(string);
			return IText.this;
		}
		
	}

}
