package sawfowl.synapse.utils;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.services.LocaleService;

public class WatchRunner {

	private static WatchRunner instance;
	private static boolean work = false;
	private static Watcher watcher;
	private boolean started = false;
	private static Logger logger;
	private WatchRunner(LocaleService localeService, Logger logger) {
		WatchRunner.logger = logger;
		watcher = new Watcher(localeService, logger);
	}

	public static void createInstance(LocaleService localeService, Logger logger) {
		if(instance == null) instance = new WatchRunner(localeService, logger);
	}

	public static WatchRunner getInstance() {
		return instance;
	}

	public static void initPlugin(PluginContainer container, Path localesPath) {
		watcher.register(container, localesPath);
	}

	public void enable() {
		work = true;
		watcher.enable();
	}

	public void run() {
		if(started) return;
		started = true;
		getLogger().info("[FileWatcher] " + SynapsePlugin.getLocales().getSystemAsReferenced().getLoggerMessages().getStartWatch());
		enable();
		CompletableFuture.runAsync(() -> {
			while(work) {
				watcher.startWatch();
			}
		});
	}

	public void stopWatch() {
		watcher.stopWatch();
		work = false;
		started = false;
	}

	private Logger getLogger() {
		return logger;
	}

	public static void pause() {
		work = !work;
		watcher.pause();
	}

}
