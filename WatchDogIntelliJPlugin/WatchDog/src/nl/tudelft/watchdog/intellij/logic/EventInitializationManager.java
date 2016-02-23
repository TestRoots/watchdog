package nl.tudelft.watchdog.intellij.logic;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.EventTransferManagerBase;
import nl.tudelft.watchdog.intellij.logic.event.EventPersister;
import nl.tudelft.watchdog.intellij.logic.event.listeners.BreakpointListener;
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
    private final EventTransferManagerBase eventTransferManager;

    /**
     * Private constructor.
     */
    private EventInitializationManager(Project project) {
        // Double getPath() because they are different methods on different objects
        File baseFolder = new File(PluginManager.getPlugin(PluginId.findId("nl.tudelft.watchdog")).getPath().getPath());

        File toTransferDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "events.mapdb");
        File statisticsDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "eventsStatistics.mapdb");

        eventsToTransferPersister = new EventPersister(toTransferDatabaseFile);
        eventsStatisticsPersister = new EventPersister(statisticsDatabaseFile);

        eventManager = new EventManager(eventsToTransferPersister, eventsStatisticsPersister);
        eventManager.setSessionSeed(IntervalInitializationManager.getInstance(project.getName())
                .getIntervalManager().getSessionSeed());

        eventTransferManager = new EventTransferManagerBase(eventsToTransferPersister, WatchDogUtils.getProjectName());

        XDebuggerManager.getInstance(project).getBreakpointManager().addBreakpointListener(new BreakpointListener(eventManager));
    }

    /**
     * Returns the existing or creates and returns a new
     * {@link EventInitializationManager} instance for the given project.
     */
    public static EventInitializationManager getInstance(Project project) {
        EventInitializationManager instance = instances.get(project.getName());
        if (instance == null) {
            instance = new EventInitializationManager(project);
            instances.put(project.getName(), instance);
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
    }

    /**
     * @return the event transfer manager.
     */
    public EventTransferManagerBase getEventTransferManager() {
        return eventTransferManager;
    }
}
