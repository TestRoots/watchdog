package nl.tudelft.watchdog.intellij.logic;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.network.TransferManagerBase;
import nl.tudelft.watchdog.core.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.intellij.logic.document.DocumentCreator;
import nl.tudelft.watchdog.intellij.logic.document.EditorWrapper;
import nl.tudelft.watchdog.intellij.logic.interval.IntervalManager;
import nl.tudelft.watchdog.intellij.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.intellij.logic.storage.Persister;
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

	private static final String PLUGIN_ID = "nl.tudelft.watchdog";
    /**
     * The map containing the InitializationManager for each open IntelliJ project.
     */
    private static volatile HashMap<String, InitializationManager> initializationManagers = new HashMap<>();

    private final Persister toTransferPersister;
    private final Persister statisticsPersister;

    /**
     * Tracks all one-time events for debugging and static analysis.
     */
    private final TrackingEventManager trackingEventManager;
    private final IntervalManager intervalManager;

    private final IntelliJListener intelliJListener;
    private final TransferManagerBase transferManager;

    /**
     * Private constructor.
     */
    private InitializationManager(Project project) {
        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.findId(PLUGIN_ID));

        if (plugin == null) {
            throw new IllegalArgumentException("Plugin id \"" + PLUGIN_ID + "\" could not be found in the list of installed plugins.");
        }

        // Double getPath() because they are different methods on different objects
        File baseFolder = new File(plugin.getPath().getPath());

        // Initialize persisters
        File toTransferDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "watchdog.mapdb");
        File statisticsDatabaseFile = new File(baseFolder, WatchDogUtils.getProjectName() + "watchdogStatistics.mapdb");

        toTransferPersister = new Persister(
                toTransferDatabaseFile);
        statisticsPersister = new Persister(
                statisticsDatabaseFile);

        // Initialize managers
        intervalManager = new IntervalManager(toTransferPersister, statisticsPersister);
        WatchDogEventType.intervalManager = intervalManager;
        WatchDogEventType.editorSpecificImplementation = new IntelliJWatchDogEventEditorSpecificImplementation();
        trackingEventManager = new TrackingEventManager(toTransferPersister, statisticsPersister);
        trackingEventManager.setSessionSeed(intervalManager.getSessionSeed());
        new TimeSynchronityChecker(intervalManager);
        transferManager = new TransferManagerBase(toTransferPersister, WatchDogUtils.getProjectName());

        // Initialize listeners
        intelliJListener = new IntelliJListener(trackingEventManager, project);
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
     * Closes the database. The database can recover even if it is not closed
     * properly, but it is good practice to close it anyway.
     */
    public void shutdown(String projectName) {
        toTransferPersister.closeDatabase();
        statisticsPersister.closeDatabase();
        Disposer.dispose(intelliJListener);
        initializationManagers.remove(projectName);
    }


    /** @return the debug event manager. */
    public TrackingEventManager getTrackingEventManager() {
        return trackingEventManager;
    }

    public TransferManagerBase getTransferManager() {
        return transferManager;
    }

    private class IntelliJWatchDogEventEditorSpecificImplementation implements WatchDogEventType.WatchDogEventEditorSpecificImplementation {
        @Override
        public void addJUnitInterval(Object source) {
            JUnitInterval junitInterval = (JUnitInterval) source;
            intervalManager.addInterval(junitInterval);
        }

        @Override
        public EditorWrapperBase createEditorWrapper(Object editor) {
            return new EditorWrapper((Editor) editor);
        }

        @Override
        public Document createDocument(Object editor) {
            return DocumentCreator.createDocument((Editor) editor);
        }
    }
}
