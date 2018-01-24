package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.eclipse.logic.InitializationManager;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.BreakpointListener;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.DebugEventListener;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;

/**
 * Sets up the listeners for eclipse UI events and registers the shutdown
 * listeners.
 */
public class WorkbenchListener {
	/** The serialization manager. */
	private TransferManager transferManager;

	/** The debug event manager used to process debug events. */
	private TrackingEventManager TrackingEventManager;

	/**
	 * The window listener. An Eclipse window is the whole Eclipse application
	 * window.
	 */
	private WindowListener windowListener;

	private IWorkbench workbench;

	/**
	 * Constructor.
	 *
	 * @param TrackingEventManager
	 */
	public WorkbenchListener(TrackingEventManager TrackingEventManager,
			TransferManager transferManager) {
		this.TrackingEventManager = TrackingEventManager;
		this.transferManager = transferManager;
		this.workbench = PlatformUI.getWorkbench();
	}

	/**
	 * Adds listeners to Workbench including already opened windows and
	 * registers shutdown and debugger listeners.
	 */
	public void attachListeners() {
		new WatchDogEvent(workbench, EventType.START_IDE).update();
		windowListener = new WindowListener();
		workbench.addWindowListener(windowListener);
		addListenersToAlreadyOpenWindows();
		new JUnitListener();
		new GeneralActivityListener(workbench.getDisplay());
		addDebuggerListeners();
		addShutdownListeners();
	}

	/** Initializes the listeners for debug intervals and events. */
	private void addDebuggerListeners() {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		debugPlugin.addDebugEventListener(
				new DebuggerListener());
		debugPlugin.getBreakpointManager().addBreakpointListener(
				new BreakpointListener(TrackingEventManager));
		debugPlugin.addDebugEventListener(
				new DebugEventListener(TrackingEventManager));
	}

	/** The shutdown listeners, executed when Eclipse is shutdown. */
	private void addShutdownListeners() {
		workbench.addWorkbenchListener(new IWorkbenchListener() {

			private InitializationManager initializationManager;

			@Override
			public boolean preShutdown(final IWorkbench workbench,
					final boolean forced) {
				initializationManager = InitializationManager.getInstance();
				new WatchDogEvent(workbench, EventType.END_IDE).update();
				initializationManager.getIntervalManager().closeAllIntervals();
				transferManager.sendItemsImmediately();
				return true;
			}

			@Override
			public void postShutdown(final IWorkbench workbench) {
				initializationManager.shutdown();
			}
		});
	}

	/**
	 * If windows are already open when the listener registration from WatchDog
	 * starts (e.g. due to saved Eclipse workspace state), add these listeners
	 * to already opened windows.
	 *
	 * This is usually the single Eclipse application window.
	 */
	private void addListenersToAlreadyOpenWindows() {
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			windowListener.windowOpened(window);
		}
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		windowListener.windowActivated(activeWindow);
	}

}
