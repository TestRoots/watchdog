package nl.tudelft.watchdog.logic;

import java.io.File;

import nl.tudelft.watchdog.Activator;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.logic.interval.IntervalTransferManager;
import nl.tudelft.watchdog.logic.network.ClientVersionChecker;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.logic.ui.listeners.WorkbenchListener;
import nl.tudelft.watchdog.ui.preferences.Preferences;

/**
 * Manages the setup process of the interval recording infrastructure. Is a
 * singleton and contains UI code. Guarantees that there is only one properly
 * initialized {@link IntervalManager} that does the real work.
 */
public class InitializationManager {

	private static final int USER_ACTIVITY_TIMEOUT = 16000;

	/** The singleton instance of the interval manager. */
	private static volatile InitializationManager instance = null;

	private final IntervalManager intervalManager;

	private final IntervalPersister intervalsToTransferPersister;

	private final IntervalPersister intervalsStatisticsPersister;

	private EventManager eventManager;

	/** Private constructor. */
	private InitializationManager() {
		WatchDogGlobals.logDirectory = "watchdog/logs/";
		WatchDogGlobals.preferences = Preferences.getInstance();
		File baseFolder = Activator.getDefault().getStateLocation().toFile();
		File toTransferDatabaseFile = new File(baseFolder, "intervals.mapdb");
		File statisticsDatabaseFile = new File(baseFolder,
				"intervalsStatistics.mapdb");

		intervalsToTransferPersister = new IntervalPersister(
				toTransferDatabaseFile);
		intervalsStatisticsPersister = new IntervalPersister(
				statisticsDatabaseFile);

		new ClientVersionChecker();
		intervalManager = new IntervalManager(intervalsToTransferPersister,
				intervalsStatisticsPersister);
		eventManager = new EventManager(intervalManager, USER_ACTIVITY_TIMEOUT);
		new TimeSynchronityChecker(intervalManager, eventManager);

		WorkbenchListener workbenchListener = new WorkbenchListener(
				eventManager, new IntervalTransferManager(
						intervalsToTransferPersister));
		workbenchListener.attachListeners();
	}

	/**
	 * Returns the existing or creates and returns a new
	 * {@link InitializationManager} instance.
	 */
	public static InitializationManager getInstance() {
		if (instance == null) {
			instance = new InitializationManager();
		}
		return instance;
	}

	/** @return the intervalManager. */
	public IntervalManager getIntervalManager() {
		return intervalManager;
	}

	/** @return the statistics interval persisters. */
	public IntervalPersister getIntervalsStatisticsPersister() {
		return intervalsStatisticsPersister;
	}

	/** @return the event Manager. */
	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Closes the database. The database can recover even if it is not closed
	 * properly, but it is good practice to close it anyway.
	 */
	public void shutdown() {
		intervalsToTransferPersister.closeDatabase();
		intervalsStatisticsPersister.closeDatabase();
	}
}
