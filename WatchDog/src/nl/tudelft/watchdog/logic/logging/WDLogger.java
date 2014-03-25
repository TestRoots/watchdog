package nl.tudelft.watchdog.logic.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;

/**
 * Wrapper class for providing logging capability.
 */
public class WDLogger {

	/** The logger instance. */
	private static Logger logger = Logger.getLogger(WDLogger.class.getName());

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
