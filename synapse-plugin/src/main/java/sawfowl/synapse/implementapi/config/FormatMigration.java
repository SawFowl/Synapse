package sawfowl.synapse.implementapi.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import sawfowl.synapse.api.config.Config;
import sawfowl.synapse.api.config.ConfigTypes;

/**
 * Moving a config or a locale to the forced format.<br>
 * The same logic was copied into both config builders and three methods of the locales list.
 * The copies matched files by {@code getName().contains(name)}, so {@code ItemsConfig.toml}
 * was taken for {@code Config}, and opened the old file with the wrong type.
 */
public final class FormatMigration {

	private FormatMigration() {}

	/**
	 * Opens {@code name} in the target format.
	 * If there is no file in that format yet, the first file with the same name in another format
	 * is converted into it and deleted, and other copies in other formats are deleted.
	 *
	 * @param open opens the config or the locale in the given format
	 */
	public static <C extends Config> C open(Path directory, String name, ConfigTypes target, Function<ConfigTypes, C> open) {
		if(Files.exists(directory.resolve(name + target))) return open.apply(target);
		C updated = open.apply(target);
		boolean migrated = false;
		for(ConfigTypes type : ConfigTypes.values()) {
			if(type == ConfigTypes.UNKNOWN || type.comparableType(target)) continue;
			Path old = directory.resolve(name + type);
			if(!Files.isRegularFile(old)) continue;
			if(!migrated) {
				try {
					ConfigurationNode oldRoot = open.apply(type).getRootNode();
					updated.getLoader().save(oldRoot);
					migrated = true;
				} catch (ConfigurateException e) {
					e.printStackTrace();
					// Keep the old file and try again next time instead of keeping the defaults.
					delete(updated.getPath());
					continue;
				}
			}
			delete(old);
		}
		if(migrated) updated.load();
		return updated;
	}

	private static void delete(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
