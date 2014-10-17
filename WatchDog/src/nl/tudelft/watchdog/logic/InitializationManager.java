package nl.tudelft.watchdog.logic;

import java.io.File;

import nl.tudelft.watchdog.Activator;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.logic.interval.IntervalTransferManager;
import nl.tudelft.watchdog.logic.network.ClientVersionChecker;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.listeners.WorkbenchListener;

/**
 * Manages the setup process of the interval recording infrastructure. Is a
 * singleton and contains UI code. Guarantees that there is only one properly
 * initialized {@link IntervalManager} that does the real work.
 */
public class InitializationManager {

	private static final int USER_ACTIVITY_TIMEOUT = 16000;

	/** The singleton instance of the interval manager. */
	private static volatile InitializationManager instance = null;

	private IntervalManager intervalManager;

	private IntervalPersister intervalsToTransferPersister;

	private IntervalPersister intervalsStatisticsPersister;

	/** Private constructor. */
	private InitializationManager() {
		File baseFolder = Activator.getDefault().getStateLocation().toFile();
		File toTransferDatabaseFile = new File(baseFolder, "intervals.mapdb");
		File statisticsDatabaseFile = new File(baseFolder,
				"intervalsStatistics.mapdb");

		intervalsToTransferPersister = new IntervalPersister(
				toTransferDatabaseFile);
		intervalsStatisticsPersister = new IntervalPersister(
				statisticsDatabaseFile);

		new ClientVersionChecker();
		this.intervalManager = new IntervalManager(
				intervalsToTransferPersister, intervalsStatisticsPersister);
		EventManager eventManager = new EventManager(intervalManager,
				USER_ACTIVITY_TIMEOUT);

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

	/**
	 * Closes the database. The database can recover even if it is not closed
	 * properly, but it is good practice to close it anyway.
	 */
	public void shutdown() {
		intervalsToTransferPersister.closeDatabase();
		intervalsStatisticsPersister.closeDatabase();
	}
}
