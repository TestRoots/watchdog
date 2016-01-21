package nl.tudelft.watchdog.intellij.logic;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.intellij.logic.event.EventPersister;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the setup process of the event recording infrastructure. Is a
 * singleton and contains UI code. Guarantees that there is only one properly
 * initialized {@link nl.tudelft.watchdog.core.logic.event.EventManager} that does the real work.
 */
public class EventInitializationManager {

    /**
     * The singleton instance of the interval initialization manager per project.
     */
    private static volatile Map<String, EventInitializationManager> instances = new HashMap<String, EventInitializationManager>();

    private final EventManager eventManager;
    private final EventPersister eventsToTransferPersister;
    private final EventPersister eventsStatisticsPersister;

    /**
     * Private constructor.
     */
    private EventInitializationManager(String sessionSeed) {
        // Double getPath() because they are different methods on different objects
        File baseFolder = new File(PluginManager.getPlugin(PluginId.findId("nl.tudelft.watchdog")).getPath().getPath());

        File toTransferDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "events.mapdb");
        File statisticsDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "eventsStatistics.mapdb");

        eventsToTransferPersister = new EventPersister(toTransferDatabaseFile);
        eventsStatisticsPersister = new EventPersister(statisticsDatabaseFile);

        eventManager = new EventManager(eventsToTransferPersister, eventsStatisticsPersister);
        eventManager.setSessionSeed(sessionSeed);

        //TODO: init listeners and transfer manager
    }

    /**
     * Returns the existing or creates and returns a new
     * {@link EventInitializationManager} instance for the given project.
     */
    public static EventInitializationManager getInstance(String projectName) {
        EventInitializationManager instance = instances.get(projectName);
        if (instance == null) {
            instance = new EventInitializationManager(IntervalInitializationManager.getInstance(projectName)
                    .getIntervalManager().getSessionSeed());
            instances.put(projectName, instance);
        }
        return instance;
    }

    /**
     * Closes the database. The database can recover even if it is not closed
     * properly, but it is good practice to close it anyway.
     */
    public void shutdown(String projectName) {
        eventsToTransferPersister.closeDatabase();
        eventsStatisticsPersister.closeDatabase();
        instances.remove(projectName);
        //TODO: remove listeners
    }
}
