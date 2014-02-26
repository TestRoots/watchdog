package nl.tudelft.watchdog.plugin.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Our wrapper class for providing logging capability.
 */
public class WDLogger {
	private static Logger log = Logger.getLogger(WDLogger.class.getName());

	/**
	 * Adds the given log handler and sets the given Level on it.
	 */
	public static void addHandlerAndSetLevel(Handler handler, Level level) {
		log.addHandler(handler);
		handler.setLevel(level);
	}

	/**
	 * Closes all log handlers.
	 */
	public static void closeAllHandlers() {
		for (Handler h : log.getHandlers()) {
			h.close();
		}
	}

	/**
	 * Logs message at warning level INFO.
	 */
	public static void logInfo(String message) {
		log.log(Level.INFO, message);
	}

	/**
	 * Logs message at warning level SEVERE.
	 */
	public static void logSevere(String message) {
		log.log(Level.SEVERE, message);
	}

	/**
	 * Logs the {@link Throwable} at warning level SEVERE.
	 */
	public static void logSevere(Throwable throwable) {
		log.log(Level.SEVERE, throwable.getMessage(), throwable);
	}

	/**
	 * Logs the message at the given warning level.
	 */
	public static void log(Level level, String message) {
		log.log(level, message);
	}

	/** Logs the message and the {@link Throwable} at the given warning level. */
	public static void log(Level level, String message, Throwable throwable) {
		log.log(level, message, throwable);
	}

}
