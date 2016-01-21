package nl.tudelft.watchdog.eclipse.logic;

import java.io.File;

import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.EventPersisterBase;
import nl.tudelft.watchdog.eclipse.Activator;

/**
 * Manages the setup process of the event recording infrastructure. Is a
 * singleton and contains UI code. Guarantees that there is only one properly
 * initialized {@link EventManager} that does the real work.
 */
public class EventInitializationManager {

	/** The singleton instance of the event initialization manager. */
	private static volatile EventInitializationManager instance = null;

	private final EventManager eventManager;
	private final EventPersisterBase eventsToTransferPersister;
	private final EventPersisterBase eventsStatisticsPersister;

	/** Private constructor. */
	private EventInitializationManager() {
		File baseFolder = Activator.getDefault().getStateLocation().toFile();
		File toTransferDatabaseFile = new File(baseFolder, "events.mapdb");
		File statisticsDatabaseFile = new File(baseFolder,
				"eventsStatistics.mapdb");

		eventsToTransferPersister = new EventPersisterBase(
				toTransferDatabaseFile);
		eventsStatisticsPersister = new EventPersisterBase(
				statisticsDatabaseFile);
		eventManager = new EventManager(eventsToTransferPersister,
				eventsStatisticsPersister);
		eventManager.setSessionSeed(IntervalInitializationManager.getInstance()
				.getIntervalManager().getSessionSeed());

		// TODO: init listeners and transfer manager
	}

	/**
	 * Returns the existing or creates and returns a new
	 * {@link EventInitializationManager} instance.
	 */
	public static EventInitializationManager getInstance() {
		if (instance == null) {
			instance = new EventInitializationManager();
		}
		return instance;
	}

	/**
	 * Closes the database. The database can recover even if it is not closed
	 * properly, but it is good practice to close it anyway.
	 */
	public void shutdown() {
		eventsToTransferPersister.closeDatabase();
		eventsStatisticsPersister.closeDatabase();
	}

}
