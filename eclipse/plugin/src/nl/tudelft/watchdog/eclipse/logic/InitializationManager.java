package nl.tudelft.watchdog.eclipse.logic;

import java.io.File;

import org.eclipse.ui.texteditor.ITextEditor;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.interval.IDEIntervalManagerBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.PerspectiveInterval;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.ui.TimeSynchronityChecker;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType.WatchDogEventEditorSpecificImplementation;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.logic.document.DocumentCreator;
import nl.tudelft.watchdog.eclipse.logic.document.EditorWrapper;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.eclipse.logic.network.ClientVersionChecker;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.WorkbenchListener;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Manages the setup process of the interval and event recording infrastructure.
 * Is a singleton and contains UI code. Guarantees that there is only one
 * properly initialized {@link IntervalManager} and {@link TrackingEventManager}
 * that do the real work.
 */
public class InitializationManager {

	/** The singleton instance. */
	private static volatile InitializationManager instance = null;

	private final PersisterBase toTransferPersister;
	private final PersisterBase statisticsPersister;

	private final TrackingEventManager trackingEventManager;
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
		trackingEventManager = new TrackingEventManager(toTransferPersister,
				statisticsPersister);
		trackingEventManager.setSessionSeed(intervalManager.getSessionSeed());
		
		WatchDogEventType.intervalManager = intervalManager;
		WatchDogEventType.editorSpecificImplementation = new EclipseWatchDogEventSpecificImplementation(intervalManager);

		new TimeSynchronityChecker(intervalManager);

		// Initialize listeners
		WorkbenchListener workbenchListener = new WorkbenchListener(
				trackingEventManager, new TransferManager(
						toTransferPersister, WatchDogUtils.getWorkspaceName()));
		workbenchListener.attachListeners();
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

	/** @return the debug event manager. */
	public TrackingEventManager getTrackingEventManager() {
		return trackingEventManager;
	}

	/**
	 * Closes the database. The database can recover even if it is not closed
	 * properly, but it is good practice to close it anyway.
	 */
	public void shutdown() {
		toTransferPersister.closeDatabase();
		statisticsPersister.closeDatabase();
	}
	
	public static final class EclipseWatchDogEventSpecificImplementation
			implements WatchDogEventEditorSpecificImplementation {
		
		private IDEIntervalManagerBase intervalManager;

		public EclipseWatchDogEventSpecificImplementation(IDEIntervalManagerBase intervalManager) {
			this.intervalManager = intervalManager;
		}

		@Override
		public EditorWrapperBase createEditorWrapper(Object editor) {
			return new EditorWrapper((ITextEditor) editor);
		}
		
		@Override
		public Document createDocument(Object editor) {
			return DocumentCreator.createDocument((ITextEditor) editor);
		}
		
		@Override
		public void addJUnitInterval(Object interval) {
			this.intervalManager.addInterval((JUnitInterval) interval);
		}
		
		@Override
		public void processPerspectiveInterval() {
			PerspectiveInterval perspectiveInt = this.intervalManager
					.getInterval(PerspectiveInterval.class);
			if (perspectiveInt != null) {
				WatchDogEventType.START_PERSPECTIVE.process(perspectiveInt.getPerspectiveType());
			}
		}
	}
}
