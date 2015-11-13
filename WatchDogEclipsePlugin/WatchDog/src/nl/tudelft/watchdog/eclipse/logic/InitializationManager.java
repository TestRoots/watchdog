package nl.tudelft.watchdog.eclipse.logic;

import java.io.File;

import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalTransferManager;
import nl.tudelft.watchdog.eclipse.logic.network.ClientVersionChecker;
import nl.tudelft.watchdog.eclipse.logic.ui.EventManager;
import nl.tudelft.watchdog.eclipse.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.WorkbenchListener;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

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

	private final IntervalPersisterBase intervalsToTransferPersister;

	private final IntervalPersisterBase intervalsStatisticsPersister;

	private EventManager eventManager;

	/** Private constructor. */
	private InitializationManager() {
		WatchDogGlobals.setLogDirectory("watchdog" + File.separator + "logs"
				+ File.separator);
		WatchDogGlobals.setPreferences(Preferences.getInstance());
		File baseFolder = Activator.getDefault().getStateLocation().toFile();
		File toTransferDatabaseFile = new File(baseFolder, "intervals.mapdb");
		File statisticsDatabaseFile = new File(baseFolder,
				"intervalsStatistics.mapdb");

		intervalsToTransferPersister = new IntervalPersisterBase(
				toTransferDatabaseFile);
		intervalsStatisticsPersister = new IntervalPersisterBase(
				statisticsDatabaseFile);

		new ClientVersionChecker();
		intervalManager = new IntervalManager(intervalsToTransferPersister,
				intervalsStatisticsPersister);
		eventManager = new EventManager(intervalManager, USER_ACTIVITY_TIMEOUT);
		new TimeSynchronityChecker(intervalManager, eventManager);

		WorkbenchListener workbenchListener = new WorkbenchListener(
				eventManager, new IntervalTransferManager(
						intervalsToTransferPersister,
						WatchDogUtils.getWorkspaceName()));
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
	public IntervalPersisterBase getIntervalsStatisticsPersister() {
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
