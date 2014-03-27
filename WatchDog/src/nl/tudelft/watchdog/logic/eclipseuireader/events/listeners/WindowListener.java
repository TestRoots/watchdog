package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.events.ImmediateNotifyingObservable;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

/** Listening for UI event ons windows. */
public class WindowListener implements IWindowListener {

	/** The eventObservable. */
	private ImmediateNotifyingObservable editorObservable;

	/** Constructor. */
	public WindowListener(ImmediateNotifyingObservable editorObservable) {
		this.editorObservable = editorObservable;
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		addPageListener(window);
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
	}

	/** Adds page listeners for all open pages of the supplied windows. */
	public void addPageListener(IWorkbenchWindow window) {
		// for new pages added in this window
		PageListener pageListener = new PageListener(editorObservable);
		window.addPageListener(pageListener);

		// for existing pages in this window
		for (IWorkbenchPage page : window.getPages()) {
			pageListener.addPartListener(page);
		}
	}
}
