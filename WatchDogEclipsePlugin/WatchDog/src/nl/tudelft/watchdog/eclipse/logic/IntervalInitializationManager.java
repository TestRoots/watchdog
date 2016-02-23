package nl.tudelft.watchdog.eclipse.logic;

import java.io.File;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalTransferManager;
import nl.tudelft.watchdog.eclipse.logic.network.ClientVersionChecker;
import nl.tudelft.watchdog.eclipse.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.WorkbenchListener;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Manages the setup process of the interval recording infrastructure. Is a
 * singleton and contains UI code. Guarantees that there is only one properly
 * initialized {@link IntervalManager} that does the real work.
 */
public class IntervalInitializationManager {

	private static final int USER_ACTIVITY_TIMEOUT = 16000;

	/** The singleton instance of the interval manager. */
	private static volatile IntervalInitializationManager instance = null;

	private final IntervalManager intervalManager;

	private final PersisterBase intervalsToTransferPersister;

	private final PersisterBase intervalsStatisticsPersister;

	private WatchDogEventManager eventManager;

	/** Private constructor. */
	private IntervalInitializationManager() {
		WatchDogGlobals.setLogDirectory(
				"watchdog" + File.separator + "logs" + File.separator);
		WatchDogGlobals.setPreferences(Preferences.getInstance());
		File baseFolder = Activator.getDefault().getStateLocation().toFile();
		File toTransferDatabaseFile = new File(baseFolder, "intervals.mapdb");
		File statisticsDatabaseFile = new File(baseFolder,
				"intervalsStatistics.mapdb");

		intervalsToTransferPersister = new PersisterBase(
				toTransferDatabaseFile);
		intervalsStatisticsPersister = new PersisterBase(
				statisticsDatabaseFile);

		new ClientVersionChecker();
		intervalManager = new IntervalManager(intervalsToTransferPersister,
				intervalsStatisticsPersister);
		eventManager = new WatchDogEventManager(intervalManager,
				USER_ACTIVITY_TIMEOUT);
		new TimeSynchronityChecker(intervalManager, eventManager);

		WorkbenchListener workbenchListener = new WorkbenchListener(
				eventManager,
				new IntervalTransferManager(intervalsToTransferPersister,
						WatchDogUtils.getWorkspaceName()));
		workbenchListener.attachListeners();
	}

	/**
	 * Returns the existing or creates and returns a new
	 * {@link IntervalInitializationManager} instance.
	 */
	public static IntervalInitializationManager getInstance() {
		if (instance == null) {
			instance = new IntervalInitializationManager();
		}
		return instance;
	}

	/** @return the intervalManager. */
	public IntervalManager getIntervalManager() {
		return intervalManager;
	}

	/** @return the statistics interval persisters. */
	public PersisterBase getIntervalsStatisticsPersister() {
		return intervalsStatisticsPersister;
	}

	/** @return the event Manager. */
	public WatchDogEventManager getEventManager() {
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
