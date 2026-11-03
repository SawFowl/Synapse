package sawfowl.synapse.commands;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.mojang.brigadier.context.CommandContext;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.commands.SynapseBrigadierCommand;
import sawfowl.synapse.api.exceptions.CommandException;
import sawfowl.synapse.api.text.callback.Pagination;
import sawfowl.synapse.api.utils.TextUtils;
import sawfowl.synapse.configure.localization.Time;

public class ProxyInfo extends AbstractCommand {

	private String os, java, javaHome;
	public ProxyInfo() {
		os = System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch");
		java = System.getProperty("java.vendor") + " " + System.getProperty("java.version");
		javaHome = System.getProperty("java.home");
	}

	@Override
	public int execute(SynapseBrigadierCommand command, CommandContext<CommandSource> context) throws CommandException {
		var locale = context.getSource() instanceof Player player ? player.getEffectiveLocale() : Locale.getDefault();
		var proxyInfo = getCommands(locale).getProxyInfo();
		var header = proxyInfo.getTitle();
		var max = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		var total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		var free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		var utilised = total - free;
		var info = Arrays.asList(
			proxyInfo.getSystem(this.os),
			proxyInfo.getJava(java),
			proxyInfo.getJavaHome(javaHome),
			getServerTime(locale),
			getUptime(locale),
			Component.empty(),
			proxyInfo.getMax(max),
			proxyInfo.getAllocated(total),
			proxyInfo.getUtilised(utilised, (utilised * 100)/total, (utilised * 100)/max),
			proxyInfo.getFree(free),
			Component.empty(),
			proxyInfo.getVelocityVersion(Synapse.getProxy().getVersion().getVersion()),
			proxyInfo.getPlugins(Synapse.getProxy().getPluginManager().getPlugins().size()),
			proxyInfo.getServers(Synapse.getProxy().getAllServers().size()),
			proxyInfo.getPlayers(Synapse.getProxy().getPlayerCount())
		);

		Pagination.builder(15)
			.content(info)
			.header(header)
			.padding('=', header.color())
			.build()
			.sendTo(context.getSource());
		
		return command.success();
	}

	private Component getServerTime(Locale locale) {
		var time = SynapsePlugin.getLocales().getAsReferenced(locale).getTime();
		var format = new SimpleDateFormat(time.getFormat(), locale);
		// The TimeZone setting existed in the locales but was never applied.
		format.setTimeZone(timeZone(time.getTimeZone()));
		return getCommands(locale).getProxyInfo().getServerTime(format.format(new Date()));
	}

	private static TimeZone timeZone(String id) {
		if(id == null || id.isBlank()) return TimeZone.getDefault();
		try {
			return TimeZone.getTimeZone(ZoneId.of(id.trim()));
		} catch (DateTimeException e) {
			return TimeZone.getDefault();
		}
	}

	private Component getUptime(Locale locale) {
		return getCommands(locale).getProxyInfo().getUptime(timeFormat(SynapsePlugin.getServerUptime(), getTime(locale)).append(Component.text(" / ")).append(timeFormat(ManagementFactory.getRuntimeMXBean().getUptime() / 1000, getTime(locale))));
	}

	private Component timeFormat(long seconds, Time time) {
		return TextUtils.timeFormat(seconds, time.getDay(), time.getHour(), time.getMinute(), time.getSecond());
	}

}
