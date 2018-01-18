package nl.tudelft.watchdog.intellij.logic;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.network.TransferManagerBase;
import nl.tudelft.watchdog.core.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.intellij.logic.interval.IntervalManager;
import nl.tudelft.watchdog.intellij.logic.storage.Persister;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.intellij.logic.ui.listeners.IntelliJListener;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Manages the setup process of the interval and event recording infrastructure. Is a
 * singleton and contains UI code. Guarantees that there is only one properly
 * initialized {@link IntervalManager} and {@link TrackingEventManager} that do the real work.
 */
public class InitializationManager {

    private static final int USER_ACTIVITY_TIMEOUT = 16000;

    /**
     * The map containing the InitializationManager for each open IntelliJ project.
     */
    private static volatile HashMap<String, InitializationManager> initializationManagers = new HashMap<String, InitializationManager>();

    private final Persister toTransferPersister;
    private final Persister statisticsPersister;

    private final WatchDogEventManager watchDogEventManager;
    private final TrackingEventManager trackingEventManager;
    private final IntervalManager intervalManager;

    private final IntelliJListener intelliJListener;
    private final TransferManagerBase transferManager;

    /**
     * Private constructor.
     */
    private InitializationManager(Project project) {
        // Initialize persisters
        // Double getPath() because they are different methods on different objects
        File baseFolder = new File(PluginManager.getPlugin(PluginId.findId("nl.tudelft.watchdog")).getPath().getPath());

        File toTransferDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "watchdog.mapdb");
        File statisticsDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "watchdogStatistics.mapdb");

        toTransferPersister = new Persister(
                toTransferDatabaseFile);
        statisticsPersister = new Persister(
                statisticsDatabaseFile);

        // Initialize managers
        intervalManager = new IntervalManager(toTransferPersister,
                statisticsPersister);
        trackingEventManager = new TrackingEventManager(toTransferPersister, statisticsPersister);
        trackingEventManager.setSessionSeed(intervalManager.getSessionSeed());
        watchDogEventManager = new WatchDogEventManager(intervalManager,
                USER_ACTIVITY_TIMEOUT);
        new TimeSynchronityChecker(intervalManager, watchDogEventManager);
        transferManager = new TransferManagerBase(toTransferPersister, WatchDogUtils.getProjectName());

        // Initialize listeners
        intelliJListener = new IntelliJListener(watchDogEventManager, trackingEventManager, project);
        intelliJListener.attachListeners();
    }

    /**
     * Returns the existing or creates and returns a new
     * {@link InitializationManager} instance.
     */
    public static InitializationManager getInstance(Project project) {
        InitializationManager instance = initializationManagers.get(project.getName());
        if (instance == null) {
            instance = new InitializationManager(project);
            initializationManagers.put(project.getName(), instance);
        }
        return instance;
    }

    /**
     * @return the intervalManager.
     */
    public IntervalManager getIntervalManager() {
        return intervalManager;
    }

    /**
     * @return the statistics interval persisters.
     */
    public Persister getStatisticsPersister() {
        return statisticsPersister;
    }

    /**
     * Closes the database. The database can recover even if it is not closed
     * properly, but it is good practice to close it anyway.
     */
    public void shutdown(String projectName) {
        toTransferPersister.closeDatabase();
        statisticsPersister.closeDatabase();
        intelliJListener.removeListeners();
        initializationManagers.remove(projectName);
    }


    public WatchDogEventManager getWatchDogEventManager() {
        return watchDogEventManager;
    }

    /** @return the debug event manager. */
    public TrackingEventManager getTrackingEventManager() {
        return trackingEventManager;
    }

    public TransferManagerBase getTransferManager() {
        return transferManager;
    }
}
