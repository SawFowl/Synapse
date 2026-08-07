package sawfowl.synapse.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.LocalisedComment;

@ConfigSerializable
public class ConfigSettings {

	public ConfigSettings(){}

	@Setting("ConfigType")
	@LocalisedComment(plugin = "synapse", path = {"ConfigComments", "ConfigType"})
	private ConfigTypes type = ConfigTypes.GEYSER_YAML;
	@Setting("ForcedUse")
	@LocalisedComment(plugin = "synapse", path = {"ConfigComments", "ForcedUse"})
	private boolean forcedUse = false;

	public ConfigTypes getType() {
		return type;
	}

	public boolean isForcedUse() {
		return forcedUse;
	}

}
