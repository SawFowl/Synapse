package sawfowl.synapse.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PerPluginSettings {

	public PerPluginSettings() {}

	@Setting("ConfigSettings")
	private ConfigSettings configSettings = new ConfigSettings();
	@Setting("LocalesSettings")
	private LocalesSettings localesSettings = new LocalesSettings();

	public ConfigSettings getConfigSettings() {
		return configSettings;
	}

	public LocalesSettings getLocalesSettings() {
		return localesSettings;
	}

}
