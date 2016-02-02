package nl.tudelft.watchdog.intellij.logic;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import nl.tudelft.watchdog.core.logic.interval.IntervalTransferManagerBase;
import nl.tudelft.watchdog.core.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.intellij.logic.interval.IntervalManager;
import nl.tudelft.watchdog.intellij.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.intellij.logic.ui.EventManager;
import nl.tudelft.watchdog.intellij.logic.ui.listeners.IntelliJListener;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Manages the setup process of the interval recording infrastructure. Is a
 * singleton and contains UI code. Guarantees that there is only one properly
 * initialized {@link IntervalManager} that does the real work.
 */
public class InitializationManager {

    private static final int USER_ACTIVITY_TIMEOUT = 16000;

    /**
     * The map containing the InitializationManager for each open IntelliJ project.
     */
    private static volatile HashMap<String, InitializationManager> initializationManagers = new HashMap<String, InitializationManager>();

    private final IntervalManager intervalManager;

    private final IntervalPersister intervalsToTransferPersister;

    private final IntervalPersister intervalsStatisticsPersister;

    private final IntervalTransferManagerBase transferManager;

    private final EventManager eventManager;

    private final IntelliJListener intelliJListener;

    /**
     * Private constructor.
     */
    private InitializationManager(Project project) {
        // Double getPath() because they are different methods on different objects
        File baseFolder = new File(PluginManager.getPlugin(PluginId.findId("nl.tudelft.watchdog")).getPath().getPath());

        File toTransferDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "intervals.mapdb");
        File statisticsDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "intervalsStatistics.mapdb");

        intervalsToTransferPersister = new IntervalPersister(
                toTransferDatabaseFile);
        intervalsStatisticsPersister = new IntervalPersister(
                statisticsDatabaseFile);

        intervalManager = new IntervalManager(intervalsToTransferPersister,
                intervalsStatisticsPersister);
        eventManager = new EventManager(intervalManager,
                USER_ACTIVITY_TIMEOUT);
        new TimeSynchronityChecker(intervalManager, eventManager);

        transferManager = new IntervalTransferManagerBase(intervalsToTransferPersister, WatchDogUtils.getProjectName());

        intelliJListener = new IntelliJListener(eventManager, project);
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
     * @return the intervalManager belonging to the project.
     */
    public static IntervalManager getIntervalManagerForProject(String projectName) {
        InitializationManager initializationManager = initializationManagers.get(projectName);
        if (initializationManager != null) {
            return initializationManager.getIntervalManager();
        }
        return null;
    }

    /**
     * @return the statistics interval persisters.
     */
    public IntervalPersister getIntervalsStatisticsPersister() {
        return intervalsStatisticsPersister;
    }

    /**
     * Closes the database. The database can recover even if it is not closed
     * properly, but it is good practice to close it anyway.
     */
    public void shutdown(String projectName) {
        intervalsToTransferPersister.closeDatabase();
        intervalsStatisticsPersister.closeDatabase();
        intelliJListener.removeListeners();
        initializationManagers.remove(projectName);
    }


    public EventManager getEventManager() {
        return eventManager;
    }

    public IntervalTransferManagerBase getTransferManager() {
        return transferManager;
    }
}

