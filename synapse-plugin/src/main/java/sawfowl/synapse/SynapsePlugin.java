package sawfowl.synapse;

import java.nio.file.Path;

import com.google.inject.Inject;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.implementapi.ISynapse;
import sawfowl.synapse.implementapi.InjectorAPI;

@Plugin(id = "synapse", authors = {"SawFowl"})
public class SynapsePlugin {

	private final SynapsePlugin instance;
	@SuppressWarnings("unused")
	private final Synapse synapse;

	@Inject
	public SynapsePlugin(ProxyServer server, @DataDirectory Path dataDirectory, PluginContainer container) {
		this.instance = this;
		new InjectorAPI(synapse = new ISynapse(instance)).createInjector();
	}

}
