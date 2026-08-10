package sawfowl.synapse.api.services;

import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.text.Placeholder;
import sawfowl.synapse.api.text.Text;

/**
 * API for working with placeholders.<br><br>
 * Placeholders will not be applied to the entire text.<br>
 * You will need to apply them to a specific text yourself,
 *  but you do not need to repeat each time what should be replaced in the text.
 */
public interface PlaceholderService {

	static PlaceholderService get() {
		return Synapse.getPlaceholderService();
	}

	/**
	 * Registration of a placeholder.
	 * 
	 * @param <T> - The type of object that will provide data for text replacement.
	 * @param clazz - It can be null if you do not need to pass an object with data to replace the text.
	 * @param id - The placeholder's ID.
	 * @param placeholder - Contains the code for performing text replacement.
	 * @return
	 */
	<T> boolean register(@Nullable Class<T> clazz, String id, Placeholder<T> placeholder);

	/**
	 * Applying placeholders to the text.
	 * 
	 * @param text - The source text in which the replacement must be performed.
	 * @param arg - The data source for text replacement.
	 * @param alt - An alternative text that can be set when replacing.
	 * @return
	 */
	<T> Text apply(Text text, T arg, Component alt);

	Text apply(Text text, Component defalt, Object... args);

	Text apply(Component component, Component alt, Object... args);

	Text apply(String string, String alt, Object... args);

	<T> Text apply(Component component, Component alt, T arg);

	<T> Text apply(String string, String alt, T arg);

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
