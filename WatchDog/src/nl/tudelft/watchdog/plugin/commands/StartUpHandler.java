package nl.tudelft.watchdog.plugin.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import nl.tudelft.watchdog.gui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.interval.IIntervalManager;
import nl.tudelft.watchdog.interval.IntervalManager;
import nl.tudelft.watchdog.interval.events.ClosingIntervalEvent;
import nl.tudelft.watchdog.interval.events.IIntervalListener;
import nl.tudelft.watchdog.interval.events.NewIntervalEvent;
import nl.tudelft.watchdog.plugin.logging.WDLogger;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.ui.IStartup;

/**
 * Handler called when an Eclipse instance with the WatchDog plugin installed
 * gets opened and activated.
 */
public class StartUpHandler implements IStartup {

	@Override
	public void earlyStartup() {
		WatchDogGlobals.isActive = true;

		setUpLogger();
		WDLogger.logInfo("Starting up...");
		IIntervalManager intervalManager = IntervalManager.getInstance();
		intervalManager.addIntervalListener(new IIntervalListener() {

			@Override
			public void onNewInterval(NewIntervalEvent evt) {
				WDLogger.logInfo("New interval: "
						+ evt.getInterval().getEditor().getTitle());
			}

			@Override
			public void onClosingInterval(ClosingIntervalEvent evt) {
				WDLogger.logInfo("Closing interval: "
						+ evt.getInterval().getDocument().getFileName()
						+ " \n " + evt.getInterval().getStart() + " - "
						+ evt.getInterval().getEnd());
			}
		});
	}

	/** Sets up the logger. */
	private void setUpLogger() {
		SimpleFormatter formatter = new SimpleFormatter();

		try {
			// TODO (MMB) Stores logs to a path in the Eclipse installation
			File parent = new File("watchdog/logs/");
			parent.mkdirs();

			FileHandler fileHandler = new FileHandler(
					"watchdog/logs/watchdoglog.log", true);
			fileHandler.setFormatter(formatter);
			Level level = Level.OFF;
			if (WatchdogPreferences.getInstance().isLoggingEnabled()) {
				level = Level.ALL;
			}
			WDLogger.addHandlerAndSetLevel(fileHandler, level);
		} catch (SecurityException e) {
			WDLogger.logSevere(e.getMessage());
		} catch (IOException e) {
			WDLogger.logSevere(e.getMessage());
		}
	}
}