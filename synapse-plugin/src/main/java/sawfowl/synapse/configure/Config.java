package sawfowl.synapse.configure;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import com.velocitypowered.api.plugin.PluginContainer;

@ConfigSerializable
public class Config {

	public Config(){}

	@Setting("ConfigSettings")
	private ConfigSettings configSettings = new ConfigSettings();
	@Setting("LocalesSettings")
	private LocalesSettings localesSettings = new LocalesSettings();
	@Setting("PerPluginSettings")
	private Map<String, PerPluginSettings> perPluginSettings = generateExample(new HashMap<String, PerPluginSettings>());

	public ConfigSettings getConfigSettings() {
		return configSettings;
	}

	public ConfigSettings getConfigSettings(PluginContainer container) {
		return getConfigSettings(container.getDescription().getId());
	}

	public LocalesSettings getLocalesSettings(PluginContainer container) {
		return getLocalesSettings(container.getDescription().getId());
	}

	public ConfigSettings getConfigSettings(String plugin) {
		return perPluginSettings.containsKey(plugin) ? perPluginSettings.get(plugin).getConfigSettings() : configSettings;
	}

	public LocalesSettings getLocalesSettings(String plugin) {
		return perPluginSettings.containsKey(plugin) ? perPluginSettings.get(plugin).getLocalesSettings() : localesSettings;
	}

	private Map<String, PerPluginSettings> generateExample(Map<String, PerPluginSettings> map) {
		map.put("pluginid", new PerPluginSettings());
		return map;
	}

}
