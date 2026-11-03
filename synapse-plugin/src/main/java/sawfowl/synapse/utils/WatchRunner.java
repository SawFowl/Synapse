package sawfowl.synapse.utils;

import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.Logger;
import sawfowl.synapse.api.services.LocaleService;

public class WatchRunner {

	private static WatchRunner instance;
	private static volatile boolean work = false;
	private static Watcher watcher;
	private volatile boolean started = false;
	private static Logger logger;
	private Thread thread;
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

	public synchronized void run() {
		if(started) return;
		started = true;
		getLogger().info("[FileWatcher] " + SynapsePlugin.getLocales().getSystemAsReferenced().getLoggerMessages().getStartWatch());
		enable();
		// A dedicated thread: the loop blocks in WatchService.take() for its whole life,
		// which is not something to do on a thread of the shared ForkJoinPool.
		thread = new Thread(() -> {
			while(work) {
				try {
					watcher.startWatch();
				} catch (InterruptedException | ClosedWatchServiceException e) {
					break;
				} catch (RuntimeException e) {
					// One bad event must not stop the watcher for good.
					getLogger().error("[FileWatcher] " + e.getLocalizedMessage(), e);
				}
			}
		}, "Synapse-FileWatcher");
		thread.setDaemon(true);
		thread.start();
	}

	public synchronized void stopWatch() {
		work = false;
		started = false;
		// Closing the service wakes the thread blocked in take().
		watcher.stopWatch();
		thread = null;
	}

	private Logger getLogger() {
		return logger;
	}

	/**
	 * Ignore file events caused by Synapse itself until {@link #resume()}.<br>
	 * Calls nest; always pair with {@link #resume()} in a finally block.
	 */
	public static void pause() {
		watcher.pause();
	}

	public static void resume() {
		watcher.resume();
	}

}
