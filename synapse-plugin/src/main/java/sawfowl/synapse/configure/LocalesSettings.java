package sawfowl.synapse.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.LocalisedComment;

@ConfigSerializable
public class LocalesSettings {

	public LocalesSettings(){}

	@Setting("ConfigType")
	@LocalisedComment(plugin = "synapse", path = {"ConfigComments", "ConfigType"})
	private ConfigTypes type = ConfigTypes.GEYSER_YAML;
	@Setting("ForcedUse")
	@LocalisedComment(plugin = "synapse", path = {"ConfigComments", "ForcedUse"})
	private boolean forcedUse = false;
	@Setting("Path")
	@LocalisedComment(plugin = "synapse", path = {"ConfigComments", "Path"})
	private String path = "{SYNAPSE_PATH}";

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
