package nl.tudelft.watchdog.intellij.logic;

import java.io.File;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import nl.tudelft.watchdog.intellij.WatchDog;
import nl.tudelft.watchdog.intellij.logic.interval.IntervalManager;
import nl.tudelft.watchdog.intellij.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.intellij.logic.interval.IntervalTransferManager;
import nl.tudelft.watchdog.intellij.logic.ui.EventManager;
import nl.tudelft.watchdog.intellij.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.intellij.logic.ui.listeners.IntelliJListener;

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

    private final IntervalTransferManager transferManager;

    private final EventManager eventManager;

    private final IntelliJListener intelliJListener;

    /** Private constructor. */
    private InitializationManager() {
        File baseFolder = new File(PluginManager.getPlugin(PluginId.findId("nl.tudelft.watchdog")).getPath().getPath());

        File toTransferDatabaseFile = new File(baseFolder, WatchDog.project.getName() + "intervals.mapdb");
        File statisticsDatabaseFile = new File(baseFolder, WatchDog.project.getName() + "intervalsStatistics.mapdb");

        intervalsToTransferPersister = new IntervalPersister(
                toTransferDatabaseFile);
        intervalsStatisticsPersister = new IntervalPersister(
                statisticsDatabaseFile);

        //new ClientVersionChecker(); TODO implement if needed
        intervalManager = new IntervalManager(intervalsToTransferPersister,
                intervalsStatisticsPersister);
        eventManager = new EventManager(intervalManager,
                USER_ACTIVITY_TIMEOUT);
        new TimeSynchronityChecker(intervalManager, eventManager);

        transferManager = new IntervalTransferManager(intervalsToTransferPersister);

        intelliJListener = new IntelliJListener(eventManager);
        intelliJListener.attachListeners();

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

    /**
     * Closes the database. The database can recover even if it is not closed
     * properly, but it is good practice to close it anyway.
     */
    public void shutdown() {
        intervalsToTransferPersister.closeDatabase();
        intervalsStatisticsPersister.closeDatabase();
        intelliJListener.removeListeners();
    }


    public EventManager getEventManager() {
        return eventManager;
    }

    public IntervalTransferManager getTransferManager() {
        return transferManager;
    }
}

