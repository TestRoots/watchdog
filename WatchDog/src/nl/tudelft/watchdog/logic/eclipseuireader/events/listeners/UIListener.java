package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.events.UserActionManager;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalTransferManager;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Sets up the listeners for eclipse UI events and registers the shutdown
 * listeners.
 */
public class UIListener {
	/** The serialization manager. */
	private IntervalTransferManager intervalTransferManager;

	/** The editorObservable. */
	private UserActionManager userActionManager;

	/**
	 * The window listener. An Eclipse window is the whole Eclipse application
	 * window.
	 */
	private WindowListener windowListener;

	/** Constructor. */
	public UIListener(UserActionManager userActionManager,
			IntervalTransferManager intervalTransferManager) {
		this.userActionManager = userActionManager;
		this.intervalTransferManager = intervalTransferManager;
	}

	/**
	 * Adds listeners to Workbench including already opened windows and
	 * registers shutdown listeners.
	 */
	public void attachListeners() {
		addShutdownListeners();
		windowListener = new WindowListener(userActionManager);
		PlatformUI.getWorkbench().addWindowListener(windowListener);
		addListenersToAlreadyOpenWindows();
	}

	/** The shutdown listeners, executed when Eclipse is shutdown. */
	private void addShutdownListeners() {
		PlatformUI.getWorkbench().addWorkbenchListener(
				new IWorkbenchListener() {

					@Override
					public boolean preShutdown(final IWorkbench workbench,
							final boolean forced) {
						IntervalManager.getInstance()
								.closeAllCurrentIntervals();
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

		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			windowListener.windowOpened(window);
		}
	}

}