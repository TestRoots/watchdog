package nl.tudelft.watchdog.logic.interval;

import java.io.File;

import nl.tudelft.watchdog.Activator;
import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.listeners.WorkbenchListener;

/**
 * Manages the setup process of the interval recording infrastructure. Is a
 * singleton and contains UI code. It is basically a proxy for guaranteeing that
 * there is only one properly initialized {@link IntervalManager} that does the
 * real work.
 */
public class IntervalInitializationManager {

	private static final int USER_ACTIVITY_TIMEOUT = 16000;

	/** The singleton instance of the interval manager. */
	private static IntervalInitializationManager instance = null;

	private IntervalManager intervalManager;

	private IntervalPersister intervalPersister;

	/** Private constructor. */
	private IntervalInitializationManager() {
		File file = new File(
				Activator.getDefault().getStateLocation().toFile(),
				"intervals.mapdb");
		intervalPersister = new IntervalPersister(file);
		DocumentFactory documentFactory = new DocumentFactory();
		this.intervalManager = new IntervalManager(intervalPersister,
				documentFactory);
		EventManager eventManager = new EventManager(intervalManager,
				documentFactory, USER_ACTIVITY_TIMEOUT);

		WorkbenchListener workbenchListener = new WorkbenchListener(
				eventManager, new IntervalTransferManager(intervalPersister));
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

	/**
	 * Closes the database. The database can recover even if it is not closed
	 * properly, but it is good practice to close it anyway.
	 */
	public void shutdown() {
		intervalPersister.closeDatabase();
	}
}
