package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.DocumentChangeListenerAttacher;
import nl.tudelft.watchdog.logic.eclipseuireader.events.ImmediateNotifyingObservable;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalTransferManager;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Sets up the listeners for eclipse UI events and registers the shutdown
 * listeners.
 */
public class UIListener {
	/** The serialization manager. */
	private IntervalTransferManager intervalTransferManager;

	/** The editorObservable. */
	private ImmediateNotifyingObservable editorObservable;

	/** The window listener bound to this UI listener. */
	private WindowListener windowListener;

	/** Constructor. */
	public UIListener(ImmediateNotifyingObservable editorObservable,
			IntervalTransferManager intervalTransferManager) {
		this.editorObservable = editorObservable;
		this.intervalTransferManager = intervalTransferManager;
		windowListener = new WindowListener(editorObservable);
	}

	/**
	 * Adds listeners to Workbench including already opened windows and
	 * registers shutdown listeners.
	 */
	public void attachListeners() {
		addShutdownListeners();
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
	 * starts (e.g. due to saved Eclispe workspace state), add these listeners
	 * to already opened windows.
	 */
	private void addListenersToAlreadyOpenWindows() {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			windowListener.addPageListener(window);
			IWorkbenchPage activePage = window.getActivePage();

			if (activePage != null) {
				IWorkbenchPart activePart = activePage.getActivePart();
				if (activePart instanceof ITextEditor) {
					editorObservable.notifyObservers(new FocusStartEditorEvent(
							activePart));
					DocumentChangeListenerAttacher
							.listenToDocumentChanges(activePart);
				}
			}
		}
	}
}