package sawfowl.synapse.api.services;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.text.Placeholder;
import sawfowl.synapse.api.text.Text;

public interface PlaceholderService {

	static PlaceholderService get() {
		return Synapse.getPlaceholderService();
	}

	<T> boolean register(Class<T> clazz, String id, Placeholder<T> placeholder);

	<T> Text apply(Text text, T arg, Component def);

	Text applySystemPlaceholders(Text text, Component def);

	Text apply(Text text, Component def, Object... args);

	Text apply(Component component, Component def, Object... args);

	Text apply(String string, String def, Object... args);

	<T> Text apply(Component component, Component def, T arg);

	<T> Text apply(String string, String def, T arg);

	enum DefaultPlaceholderKeys {

		PLAYER_NAME {
			@Override
			public String textKey() {
				return "%player-name%";
			}
			@Override
			public String id() {
				return "PlayerName";
			}
		},
		PLAYER_PING {
			@Override
			public String textKey() {
				return "%player-ping%";
			}
			@Override
			public String id() {
				return "PlayerPing";
			}
		},
		PLAYER_SERVER {
			@Override
			public String textKey() {
				return "%player-server%";
			}
			@Override
			public String id() {
				return "PlayerServer";
			}
		},
		PLAYER_CLIENT {
			@Override
			public String textKey() {
				return "%player-client%";
			}
			@Override
			public String id() {
				return "PlayerClient";
			}
		},
		PLAYER_BALANCE {
			@Override
			public String textKey() {
				return "%player-balance%";
			}
			@Override
			public String id() {
				return "PlayerBalance";
			}
		},
		ONLINE_PLAYERS {
			@Override
			public String textKey() {
				return "%online-players%";
			}
			@Override
			public String id() {
				return "OnlinePlayers";
			}
		};

		public abstract String textKey();
		public abstract String id();

	}

}
