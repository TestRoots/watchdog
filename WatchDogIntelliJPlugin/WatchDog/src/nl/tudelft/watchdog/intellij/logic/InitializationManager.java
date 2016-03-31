package nl.tudelft.watchdog.intellij.logic;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.network.TransferManagerBase;
import nl.tudelft.watchdog.core.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.intellij.logic.event.listeners.BreakpointListener;
import nl.tudelft.watchdog.intellij.logic.event.listeners.DebugActionListener;
import nl.tudelft.watchdog.intellij.logic.event.listeners.DebugEventListener;
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
 * initialized {@link IntervalManager} and {@link EventManager} that do the real work.
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
    private final EventManager eventManager;
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
        eventManager = new EventManager(toTransferPersister, statisticsPersister);
        eventManager.setSessionSeed(intervalManager.getSessionSeed());
        watchDogEventManager = new WatchDogEventManager(intervalManager,
                USER_ACTIVITY_TIMEOUT);
        new TimeSynchronityChecker(intervalManager, watchDogEventManager);
        transferManager = new TransferManagerBase(toTransferPersister, WatchDogUtils.getProjectName());

        // Initialize listeners
        intelliJListener = new IntelliJListener(watchDogEventManager, project);
        intelliJListener.attachListeners();
        XDebuggerManager.getInstance(project).getBreakpointManager().addBreakpointListener(new BreakpointListener(eventManager));
        DebuggerManagerEx.getInstanceEx(project).getContextManager().addListener(new DebugEventListener(eventManager));
        ActionManager.getInstance().addAnActionListener(new DebugActionListener(eventManager));
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

    public TransferManagerBase getTransferManager() {
        return transferManager;
    }
}

