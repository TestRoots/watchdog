package nl.tudelft.watchdog.eclipse.logic;

import java.io.File;

import org.eclipse.debug.core.DebugPlugin;

import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.BreakpointListener;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.DebugEventListener;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.logic.network.ClientVersionChecker;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;
import nl.tudelft.watchdog.eclipse.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.WorkbenchListener;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Manages the setup process of the interval and event recording infrastructure.
 * Is a singleton and contains UI code. Guarantees that there is only one
 * properly initialized {@link IntervalManager} and {@link EventManager} that do
 * the real work.
 */
public class InitializationManager {

	private static final int USER_ACTIVITY_TIMEOUT = 16000;

	/** The singleton instance. */
	private static volatile InitializationManager instance = null;

	private final PersisterBase toTransferPersister;
	private final PersisterBase statisticsPersister;

	private final WatchDogEventManager watchDogEventManager;
	private final EventManager eventManager;
	private final IntervalManager intervalManager;

	/** Private constructor. */
	private InitializationManager() {
		WatchDogGlobals.setLogDirectory(
				"watchdog" + File.separator + "logs" + File.separator);
		WatchDogGlobals.setPreferences(Preferences.getInstance());

		// Initialize persisters
		File baseFolder = Activator.getDefault().getStateLocation().toFile();
		File toTransferDatabaseFile = new File(baseFolder, "watchdog.mapdb");
		File statisticsDatabaseFile = new File(baseFolder,
				"watchdogStatistics.mapdb");
		toTransferPersister = new PersisterBase(toTransferDatabaseFile);
		statisticsPersister = new PersisterBase(statisticsDatabaseFile);

		// Initialize managers
		new ClientVersionChecker();
		intervalManager = new IntervalManager(toTransferPersister,
				statisticsPersister);
		eventManager = new EventManager(toTransferPersister,
				statisticsPersister);
		eventManager.setSessionSeed(intervalManager.getSessionSeed());

		watchDogEventManager = new WatchDogEventManager(intervalManager,
				USER_ACTIVITY_TIMEOUT);
		new TimeSynchronityChecker(intervalManager, watchDogEventManager);

		// Initialize listeners
		WorkbenchListener workbenchListener = new WorkbenchListener(
				watchDogEventManager, new TransferManager(toTransferPersister,
						WatchDogUtils.getWorkspaceName()));
		workbenchListener.attachListeners();
		DebugPlugin.getDefault().getBreakpointManager()
				.addBreakpointListener(new BreakpointListener(eventManager));
		DebugPlugin.getDefault()
				.addDebugEventListener(new DebugEventListener(eventManager));
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

	/** @return the statistics persister. */
	public PersisterBase getStatisticsPersister() {
		return statisticsPersister;
	}

	/** @return the WatchDog event manager. */
	public WatchDogEventManager getWatchDogEventManager() {
		return watchDogEventManager;
	}

	/**
	 * Closes the database. The database can recover even if it is not closed
	 * properly, but it is good practice to close it anyway.
	 */
	public void shutdown() {
		toTransferPersister.closeDatabase();
		statisticsPersister.closeDatabase();
	}
}
