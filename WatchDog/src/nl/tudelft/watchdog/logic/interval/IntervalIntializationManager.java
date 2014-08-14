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
public class IntervalIntializationManager {

	/** The singleton instance of the interval manager. */
	private static IntervalIntializationManager instance = null;

	private IntervalManager intervalManager;

	/** Private constructor. */
	private IntervalIntializationManager() {
		File file = new File(
				Activator.getDefault().getStateLocation().toFile(),
				"intervals.mapdb");
		IntervalPersister intervalPersister = new IntervalPersister(file);
		this.intervalManager = new IntervalManager(intervalPersister,
				new DocumentFactory());
		EventManager eventManager = new EventManager(intervalManager);

		WorkbenchListener workbenchListener = new WorkbenchListener(
				eventManager, new IntervalTransferManager(intervalPersister));
		workbenchListener.attachListeners();
	}

	/**
	 * Returns the existing or creates and returns a new
	 * {@link IntervalIntializationManager} instance.
	 */
	public static IntervalIntializationManager getInstance() {
		if (instance == null) {
			instance = new IntervalIntializationManager();
		}
		return instance;
	}

	/** @return the intervalManager. */
	public IntervalManager getIntervalManager() {
		return intervalManager;
	}
}
