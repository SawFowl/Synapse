package sawfowl.synapse.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Commands {

	public Commands() {}

	@Setting("proxysudo")
	private CommandConfig proxysudo = new CommandConfig("proxysudo", "psudo", "gsudo");
	@Setting("proxyinfo")
	private CommandConfig proxyinfo = new CommandConfig("proxyinfo", "pinfo", "ginfo");
	@Setting("proxytell")
	private CommandConfig proxytell = new CommandConfig("proxytell", "ptell", "gtell");
	@Setting("proxybroadcast")
	private CommandConfig broadcast = new CommandConfig("globalbroadcast", "broadcast");
	@Setting("server")
	private CommandConfig server = new CommandConfig("server", "connect");

	public CommandConfig getProxysudo() {
		return proxysudo;
	}

	public CommandConfig getProxyinfo() {
		return proxyinfo;
	}

	public CommandConfig getProxytell() {
		return proxytell;
	}

	public CommandConfig getBroadcast() {
		return broadcast;
	}

	public CommandConfig getServer() {
		return server;
	}


}
