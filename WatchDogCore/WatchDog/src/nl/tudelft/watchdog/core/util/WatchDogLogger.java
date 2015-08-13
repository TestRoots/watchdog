package nl.tudelft.watchdog.core.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** Wrapper class for providing logging capability. */
public class WatchDogLogger {

	/** The logger instance. */
	private Logger logger;

	/** Determines whether the logger is setup. */
	private boolean isLoggerSetup = false;

	private File logDirectory = new File(WatchDogGlobals.getLogDirectory()); 

	/** The singleton instance of the logger. */
	private static volatile WatchDogLogger instance = null;

	/** Private Constructor. */
	private WatchDogLogger(boolean isLoggingEnabled) {
		try {
			if (!isLoggingEnabled) {
				// If logging is not enabled in the preferences: Abort setting
				// up the logger
				return;
			}
		} catch (NoClassDefFoundError error) {
			// We purposefully capture an error here.
			// There was an error in creating the preferences instance, so
			// Eclipse is not running. In this case, always set up the logger.
		} catch (NullPointerException error) {
			// We purposefully capture an error here.
			// There was an error in creating the preferences instance, so
			// Eclipse is not running. In this case, always set up the logger.
		}

        this.isLoggerSetup = true;
		this.logger = Logger.getLogger(WatchDogLogger.class.getName());
		logInfo("Starting up WatchDogLogger...");

		try {
			logDirectory.mkdirs();

			FileHandler fileHandler = new FileHandler(
					"watchdog/logs/watchdoglog.log", true);
			fileHandler.setFormatter(new SimpleFormatter());
			addHandlerAndSetLevel(fileHandler, Level.ALL);
		} catch (SecurityException e) {
			logSevere(e.getMessage());
		} catch (IOException e) {
			logSevere(e.getMessage());
		}
	}

	/**
	 * When this method is called, it's necessary to provide boolean value from local preferences.
	 * @param Whether or not logging is enabled.
	 * @return The instance of the single WatchDogLogger.
	 */
	public static WatchDogLogger getInstance(boolean isLoggingEnabled) {
		if (instance == null) {
			instance = new WatchDogLogger(isLoggingEnabled);
		}
		return instance;
	}

	/** Adds the given log handler and sets the given Level on it. */
	public void addHandlerAndSetLevel(Handler handler, Level level) {
		if (!isLoggerSetup) {
			return;
		}
		logger.addHandler(handler);
		handler.setLevel(level);
	}

	/** Closes all log handlers. */
	public void closeAllHandlers() {
		for (Handler handler : logger.getHandlers()) {
			handler.close();
		}
	}

	/** Logs message at warning level INFO. */
	public void logInfo(String message) {
		if (!isLoggerSetup) {
			return;
		}
		logger.log(Level.INFO, message);
	}

	/** Logs message at warning level SEVERE. */
	public void logSevere(String message) {
		if (!isLoggerSetup) {
			return;
		}
		logger.log(Level.SEVERE, message);
	}

	/** Logs the {@link Throwable} at warning level SEVERE. */
	public void logSevere(Throwable throwable) {
		if (!isLoggerSetup) {
			return;
		}
		logger.log(Level.SEVERE, throwable.getMessage(), throwable);
	}

	/** Logs the message at the given warning level. */
	public void log(Level level, String message) {
		if (!isLoggerSetup) {
			return;
		}
		logger.log(level, message);
	}

	/** Logs the message and the {@link Throwable} at the given warning level. */
	public void log(Level level, String message, Throwable throwable) {
		if (!isLoggerSetup) {
			return;
		}
		logger.log(level, message, throwable);
	}

	/** @return the path where all the log files are stored. */
	public String getLogDirectoryPath() {
		return logDirectory.getAbsolutePath();
	}

}
