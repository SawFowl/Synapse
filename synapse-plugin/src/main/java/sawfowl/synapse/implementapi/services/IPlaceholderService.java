package sawfowl.synapse.implementapi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.services.PlaceholderService;
import sawfowl.synapse.api.text.Placeholder;
import sawfowl.synapse.api.text.Text;
import sawfowl.synapse.api.utils.TextUtils;

public class IPlaceholderService implements PlaceholderService {

	/** Superclasses and interfaces of a class, computed once per class instead of on every message. */
	private static final ClassValue<List<Class<?>>> HIERARCHY = new ClassValue<>() {
		@Override
		protected List<Class<?>> computeValue(Class<?> type) {
			List<Class<?>> hierarchy = new ArrayList<>(ClassUtils.getAllSuperclasses(type));
			hierarchy.addAll(ClassUtils.getAllInterfaces(type));
			return List.copyOf(hierarchy);
		}
	};

	private final Map<Class<?>, Map<String, Placeholder<?>>> PLACEHOLDERS = new ConcurrentHashMap<>();
	private final Map<String, Placeholder<?>> SYSTEM_PLACEHOLDERS = new ConcurrentHashMap<>();
	public IPlaceholderService() {
		register(
			Player.class,
			DefaultPlaceholderKeys.PLAYER_NAME,
			(original, player, _) -> original.replace(DefaultPlaceholderKeys.PLAYER_NAME.textKey(), player.getUsername())
		);
		register(
			Player.class,
			DefaultPlaceholderKeys.PLAYER_PING,
			(original, player, _) -> original.replace(DefaultPlaceholderKeys.PLAYER_PING.textKey(), player.getPing())
		);
		register(
			Player.class,
			DefaultPlaceholderKeys.PLAYER_SERVER,
			(original, player, _) -> original.replace(DefaultPlaceholderKeys.PLAYER_SERVER.textKey(), player.getCurrentServer().map(server -> server.getServerInfo().getName()).orElse("-"))
		);
		register(
			Player.class,
			DefaultPlaceholderKeys.PLAYER_CLIENT,
			(original, player, _) -> original.replace(DefaultPlaceholderKeys.PLAYER_CLIENT.textKey(), player.getClientBrand())
		);
	/*	register(
			Player.class,
			DefaultPlaceholderKeys.PLAYER_BALANCE,
			(original, player, _) -> original.replace(DefaultPlaceholderKeys.PLAYER_BALANCE.textKey(), player.getClientBrand())
		);*/
		register(
			null,
			DefaultPlaceholderKeys.ONLINE_PLAYERS,
			(original, _, _) -> original.replace(DefaultPlaceholderKeys.ONLINE_PLAYERS.textKey(), Synapse.getProxy().getPlayerCount())
		);
	}

	@Override
	public <T> boolean register(Class<T> clazz, String id, Placeholder<T> placeholder) {
		if(clazz == null) {
			if(SYSTEM_PLACEHOLDERS.containsKey(id)) return false;
			SYSTEM_PLACEHOLDERS.put(id, placeholder);
		} else {
			if(PLACEHOLDERS.containsKey(clazz) && PLACEHOLDERS.get(clazz).containsKey(id)) return false;
			PLACEHOLDERS.computeIfAbsent(clazz, _ -> new ConcurrentHashMap<>()).put(id, placeholder);
		}
		return true;
	}

	public <T> boolean register(Class<T> clazz, DefaultPlaceholderKeys key, Placeholder<T> placeholder) {
		return register(clazz, key.id(), placeholder);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> Text apply(Text text, T arg, Component def) {
		Class<?> clazz = arg.getClass();
		if(PLACEHOLDERS.containsKey(clazz)) PLACEHOLDERS.get(arg.getClass()).values().forEach(placeholder -> ((Placeholder<T>) placeholder).apply(text, arg, def));
		// Superclasses and interfaces of the argument; getClasses() used to return nested classes instead.
		for(Class<?> clazz2 : HIERARCHY.get(clazz)) {
			if(PLACEHOLDERS.containsKey(clazz2)) PLACEHOLDERS.get(clazz2).values().forEach(placeholder -> applyOther(text, cast(arg), placeholder, def));
		}
		applySystemPlaceholders(text, def);
		return text;
	}

	public Text applySystemPlaceholders(Text text, Component def) {
		SYSTEM_PLACEHOLDERS.values().forEach(placeholder -> placeholder.apply(text, null, def));
		return text;
	}

	@Override
	public Text apply(Text text, Component def, Object... args) {
		for(Object arg : args) apply(text, arg, def);
		return text;
	}

	@Override
	public Text apply(Component component, Component def, Object... args) {
		return apply(Text.of(component), def, args);
	}

	@Override
	public Text apply(String string, String def, Object... args) {
		return apply(Text.of(string), TextUtils.deserialize(def), args);
	}

	@Override
	public <T> Text apply(Component component, Component def, T arg) {
		return apply(Text.of(component), arg, def);
	}

	@Override
	public <T> Text apply(String string, String def, T arg) {
		return apply(Text.of(string), arg, TextUtils.deserialize(def));
	}

	private <T> Text applyOther(Text text, T arg, Placeholder<? extends T> placeholder, Component def) {
		return cast(placeholder).apply(text, arg, def);
	}

	@SuppressWarnings("unchecked")
	private <T, C extends T> C cast(T arg) {
		return (C) arg;
	}

	@SuppressWarnings("unchecked")
	private <T> Placeholder<T> cast(Placeholder<?> placeholder) {
		return (Placeholder<T>) placeholder;
	}

}
