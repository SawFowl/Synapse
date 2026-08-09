package sawfowl.synapse.implementapi.text;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.text.callback.Callback;
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
		this.component.append(component);
		return this;
	}

	@Override
	public Text append(Text text) {
		return append(text.get());
	}

	@Override
	public Text replace(String key, String value) {
		component = component.replaceText(TextReplacementConfig.builder().match(key).replacement(value).build());
		return this;
	}

	@Override
	public Text replace(String key, Component value) {
		component = component.replaceText(TextReplacementConfig.builder().match(key).replacement(value).build());
		return this;
	}

	@Override
	public Text replace(String key, Text value) {
		component = component.replaceText(TextReplacementConfig.builder().match(key).replacement(value.get()).build());
		return this;
	}

	@Override
	public Text replace(String key, Object value) {
		component = component.replaceText(TextReplacementConfig.builder().match(key).replacement(value.toString()).build());
		return this;
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
		component.clickEvent(Callback.of(callback));
		return this;
	}

	@Override
	public Text createCallBack(Runnable runnable) {
		createCallBack(_ -> runnable.run());
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
		return IntStream.range(0, keys.length).boxed().collect(Collectors.toMap(i -> keys[i], i -> values.length > i ? values[i].toString() : ""));
	}

	private Map<String, Component> replaceMapComponents(String[] keys, Component[] values) {
		return IntStream.range(0, keys.length).boxed().collect(Collectors.toMap(i -> keys[i], i -> values.length > i ? values[i] : Component.empty()));
	}

	private Map<String, Component> replaceMapTexts(String[] keys, Text[] values) {
		return IntStream.range(0, keys.length).boxed().collect(Collectors.toMap(i -> keys[i], i -> values.length > i ? values[i].get() : Component.empty()));
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
