package sawfowl.synapse.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.LocalizedComment;

@ConfigSerializable
public class LocalesSettings {

	public LocalesSettings(){}

	@Setting("ConfigType")
	@LocalizedComment(plugin = "synapse", path = {"ConfigComments", "ConfigType"})
	private ConfigTypes type = ConfigTypes.GEYSER_YAML;
	@Setting("ForcedUse")
	@LocalizedComment(plugin = "synapse", path = {"ConfigComments", "ForcedUse"})
	private boolean forcedUse = false;
	@Setting("Path")
	@LocalizedComment(plugin = "synapse", path = {"ConfigComments", "Path"})
	private String path = "{PLUGIN_CONFIG_PATH}";

	public ConfigTypes getType() {
		return type;
	}

	public boolean isForcedUse() {
		return forcedUse;
	}

	public String getPath() {
		return path;
	}

}
