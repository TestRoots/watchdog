package nl.tudelft.watchdog.ui.commands;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
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

		IntervalManager.getInstance();
		WatchDogLogger.setUpLogger();
	}

}