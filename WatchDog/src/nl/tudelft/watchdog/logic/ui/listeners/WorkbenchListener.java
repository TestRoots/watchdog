package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalTransferManager;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent.EventType;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Sets up the listeners for eclipse UI events and registers the shutdown
 * listeners.
 */
public class WorkbenchListener {
	/** The serialization manager. */
	private IntervalTransferManager intervalTransferManager;

	/** The editorObservable. */
	private EventManager eventManager;

	/**
	 * The window listener. An Eclipse window is the whole Eclipse application
	 * window.
	 */
	private WindowListener windowListener;

	private IWorkbench workbench;

	/** Constructor. */
	public WorkbenchListener(EventManager userActionManager,
			IntervalTransferManager intervalTransferManager) {
		this.eventManager = userActionManager;
		this.intervalTransferManager = intervalTransferManager;
		this.workbench = PlatformUI.getWorkbench();
	}

	/**
	 * Adds listeners to Workbench including already opened windows and
	 * registers shutdown listeners.
	 */
	public void attachListeners() {
		eventManager.update(new WatchDogEvent(workbench,
				EventType.START_ECLIPSE));
		windowListener = new WindowListener(eventManager);
		workbench.addWindowListener(windowListener);
		addListenersToAlreadyOpenWindows();
		new JUnitListener(eventManager);
		new GeneralActivityListener(eventManager, workbench.getDisplay());
		addShutdownListeners();
	}

	/** The shutdown listeners, executed when Eclipse is shutdown. */
	private void addShutdownListeners() {
		workbench.addWorkbenchListener(new IWorkbenchListener() {

			@Override
			public boolean preShutdown(final IWorkbench workbench,
					final boolean forced) {
				eventManager.update(new WatchDogEvent(workbench,
						EventType.END_ECLIPSE));
				IntervalManager.getInstance().closeAllCurrentIntervals();
				intervalTransferManager.sendIntervalsImmediately();
				return true;
			}

			@Override
			public void postShutdown(final IWorkbench workbench) {
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