package nl.tudelft.watchdog.logic.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;

/**
 * Wrapper class for providing logging capability.
 */
public class WatchDogLogger {

	/** The singleton instance. */
	WatchDogLogger instance = null;

	/** Private Constructor. */
	private WatchDogLogger() {
	}

	/**
	 * Returns and, if not already existent, creates the single WatchDogLogger
	 * instance.
	 */
	public WatchDogLogger getInstance() {
		if (instance == null) {
			instance = new WatchDogLogger();
		}
		return instance;
	}

	/** The logger instance. */
	private static Logger logger = Logger.getLogger(WatchDogLogger.class
			.getName());

	/** Sets up the logger, if logging is enabled in the WatchDog Preferences. */
	public static void setUpLogger() {
		if (!WatchdogPreferences.getInstance().isLoggingEnabled()) {
			return;
		}

		logInfo("Starting up...");
		IntervalManager.getInstance().addIntervalListener(
				new IntervalLoggerObserver());

		try {
			// TODO (MMB) Stores logs to a path in the Eclipse installation
			File parent = new File("watchdog/logs/");
			parent.mkdirs();

			FileHandler fileHandler = new FileHandler(
					"watchdog/logs/watchdoglog.log", true);
			fileHandler.setFormatter(new SimpleFormatter());
			Level level = Level.OFF;
			if (WatchdogPreferences.getInstance().isLoggingEnabled()) {
				level = Level.ALL;
			}
			addHandlerAndSetLevel(fileHandler, level);
		} catch (SecurityException e) {
			logSevere(e.getMessage());
		} catch (IOException e) {
			logSevere(e.getMessage());
		}
	}

	/** Adds the given log handler and sets the given Level on it. */
	public static void addHandlerAndSetLevel(Handler handler, Level level) {
		logger.addHandler(handler);
		handler.setLevel(level);
	}

	/** Closes all log handlers. */
	public static void closeAllHandlers() {
		for (Handler h : logger.getHandlers()) {
			h.close();
		}
	}

	/** Logs message at warning level INFO. */
	public static void logInfo(String message) {
		if (!WatchdogPreferences.getInstance().isLoggingEnabled()) {
			return;
		}
		logger.log(Level.INFO, message);
	}

	/** Logs message at warning level SEVERE. */
	public static void logSevere(String message) {
		if (!WatchdogPreferences.getInstance().isLoggingEnabled()) {
			return;
		}
		logger.log(Level.SEVERE, message);
	}

	/** Logs the {@link Throwable} at warning level SEVERE. */
	public static void logSevere(Throwable throwable) {
		if (!WatchdogPreferences.getInstance().isLoggingEnabled()) {
			return;
		}
		logger.log(Level.SEVERE, throwable.getMessage(), throwable);
	}

	/** Logs the message at the given warning level. */
	public static void log(Level level, String message) {
		if (!WatchdogPreferences.getInstance().isLoggingEnabled()) {
			return;
		}
		logger.log(level, message);
	}

	/** Logs the message and the {@link Throwable} at the given warning level. */
	public static void log(Level level, String message, Throwable throwable) {
		if (!WatchdogPreferences.getInstance().isLoggingEnabled()) {
			return;
		}
		logger.log(level, message, throwable);
	}

}
