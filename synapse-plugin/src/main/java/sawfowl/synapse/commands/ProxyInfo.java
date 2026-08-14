package sawfowl.synapse.commands;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

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
		var header = getCommands(locale).getProxyInfo().getTitle();
		var max = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		var total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		var free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		var utilised = total - free;
		var info = Arrays.asList(
			getCommands(locale).getProxyInfo().getSystem(this.os),
			getCommands(locale).getProxyInfo().getJava(java),
			getCommands(locale).getProxyInfo().getJavaHome(javaHome),
			getCommands(locale).getProxyInfo().getVelocityVersion(Synapse.getProxy().getVersion().getVersion()),
			getServerTime(locale),
			getUptime(locale),

			getCommands(locale).getProxyInfo().getMax(max),
			getCommands(locale).getProxyInfo().getAllocated(total),
			getCommands(locale).getProxyInfo().getUtilised(utilised, (utilised * 100)/total, (utilised * 100)/max),
			getCommands(locale).getProxyInfo().getFree(Runtime.getRuntime().freeMemory() / 1024 / 1024),

			getCommands(locale).getProxyInfo().getPlugins(Synapse.getProxy().getPluginManager().getPlugins().size()),
			getCommands(locale).getProxyInfo().getServers(Synapse.getProxy().getAllServers().size()),
			getCommands(locale).getProxyInfo().getPlayers(Synapse.getProxy().getPlayerCount())
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
		var format = new SimpleDateFormat(SynapsePlugin.getLocales().getAsReferenced(locale).getTime().getFormat());
		var calendar = Calendar.getInstance(locale);
		calendar.setTimeInMillis(System.currentTimeMillis());
		return getCommands(locale).getProxyInfo().getServerTime(format.format(calendar.getTime()));
	}

	private Component getUptime(Locale locale) {
		return getCommands(locale).getProxyInfo().getUptime(timeFormat(SynapsePlugin.getServerUptime(), getTime(locale)).append(Component.text(" / ")).append(timeFormat(ManagementFactory.getRuntimeMXBean().getUptime() / 1000, getTime(locale))));
	}

	private Component timeFormat(long seconds, Time time) {
		return TextUtils.timeFormat(seconds, time.getDay(), time.getHour(), time.getMinute(), time.getSecond());
	}

}
