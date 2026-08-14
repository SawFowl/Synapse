package sawfowl.synapse.configure.localization.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.config.locale.Translation;

@ConfigSerializable
public class ProxyInfo implements Translation {

	private static final String TIME = "%time%";
	private static final String SYSTEM = "%system%";
	private static final String JAVA = "%java%";
	private static final String HOME = "%home%";
	private static final String VERSION = "%version%";
	private static final String PLUGINS = "%plugins%";
	private static final String SERVERS = "%servers%";
	private static final String PLAYERS = "%players%";
	private static final String ALLOCATED = "%allocated%";
	private static final String MAX = "%max%";
	private static final String UTILISED = "%utilised%";
	private static final String FREE = "%free%";
	public static ProxyInfo createRu() {
		ProxyInfo info = new ProxyInfo();
		info.title = info.deserialize("&3&lИнформация о прокси");
		info.serverTime = info.deserialize("&aВремя сервера&f: &e%time%");
		info.uptime = info.deserialize("&aАптайм / JVM&f: &e%time%");
		info.system = info.deserialize("&aСистема&f: &e%system%");
		info.java = info.deserialize("&aJava&f: &e%java%");
		info.javaHome = info.deserialize("&aJava home&f: &e%home%");
		info.velocityVersion = info.deserialize("&aВерсия Velocity&f: &e%version%");
		info.plugins = info.deserialize("&eПлагинов&f: &b%plugins%&e");
		info.servers = info.deserialize("&eСерверов&f: &b%servers%&e");
		info.players = info.deserialize("&eИгроков&f: &b%players%&e");
		info.max = info.deserialize("&aМаксимум(JVM) RAM&f: &e%max%Mb");
		info.allocated = info.deserialize("&aВыделено RAM&f: &e%allocated%Mb");
		info.utilised = info.deserialize("&aИспользуется RAM&f: &e%utilised%Mb(&6%allocated%%&e от выделенной, &6%max%%&e от максимума)");
		info.free = info.deserialize("&aСвободная (но выделенная) память&f: &e%free%Mb");
		return info;
	}

	public ProxyInfo(){}

	@Setting("Title")
	private Component title = deserialize("&3&lProxyServer info");
	@Setting("ServerTime")
	private Component serverTime = deserialize("&aServer time&f: &e%time%");
	@Setting("Uptime")
	private Component uptime = deserialize("&aUptime / JVM&f: &e%time%");
	@Setting("System")
	private Component system = deserialize("&aSystem&f: &e%system%");
	@Setting("Java")
	private Component java = deserialize("&aJava&f: &e%java%");
	@Setting("JavaHome")
	private Component javaHome = deserialize("&aJava home&f: &e%home%");
	@Setting("VelocityVersion")
	private Component velocityVersion = deserialize("&aVelocity version&f: &e%version%");
	@Setting("Plugins")
	private Component plugins = deserialize("&ePlugins&f: &b%plugins%&e");
	@Setting("Servers")
	private Component servers = deserialize("&eServers&f: &b%servers%&e");
	@Setting("Players")
	private Component players = deserialize("&ePlayers&f: &b%players%&e");
	@Setting("MemoryMax")
	private Component max = deserialize("&aMax(JVM) RAM&f: &e%max%Mb");
	@Setting("MemoryAllocated")
	private Component allocated = deserialize("&aAllocated RAM&f: &e%allocated%Mb");
	@Setting("MemoryUtilised")
	private Component utilised = deserialize("&aUtilised RAM&f: &e%utilised%Mb(&6%allocated%%&e of used, &6%max%%&e of max)");
	@Setting("MemoryFree")
	private Component free = deserialize("&aFree (but allocated) memory&f: &e%free%Mb");

	public Component getTitle() {
		return title;
	}

	public Component getServerTime(String value) {
		return replace(serverTime, TIME, value);
	}

	public Component getUptime(Component value) {
		return replace(uptime, TIME, value);
	}

	public Component getSystem(String value) {
		return replace(system, SYSTEM, value);
	}

	public Component getJava(String value) {
		return replace(java, JAVA, value);
	}

	public Component getJavaHome(String value) {
		return replace(javaHome, HOME, value);
	}

	public Component getVelocityVersion(String version) {
		return replace(velocityVersion, VERSION, version);
	}

	public Component getPlugins(Object value) {
		return replace(plugins, PLUGINS, value);
	}

	public Component getServers(Object value) {
		return replace(servers, SERVERS, value);
	}

	public Component getPlayers(Object value) {
		return replace(players, PLAYERS, value);
	}

	public Component getMax(long value) {
		return replace(max, MAX, value);
	}

	public Component getAllocated(long value) {
		return replace(allocated, ALLOCATED, value);
	}

	public Component getUtilised(long value, long allocated, long max) {
		return replace(utilised, new String[] {UTILISED, ALLOCATED, MAX}, value, allocated, max);
	}

	public Component getFree(long value) {
		return replace(free, FREE, value);
	}

}
