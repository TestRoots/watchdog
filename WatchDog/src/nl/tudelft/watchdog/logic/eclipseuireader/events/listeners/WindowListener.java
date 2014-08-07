package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.events.UserActionManager;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

/** Listening for UI event on Eclipse windows. */
public class WindowListener implements IWindowListener {

	/** The eventObservable. */
	private UserActionManager userActionManager;
	private PageListener pageListener;

	/** Constructor. */
	public WindowListener(UserActionManager userActionManager) {
		this.userActionManager = userActionManager;
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		addPageListener(window);
		addPerspectiveListener(window);
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		// TODO (MMB) Generate new Eclipse Active interval
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		window.removePageListener(pageListener);
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		// TODO (MMB) Generate new Eclipse Active interval
	}

	/** Adds page listeners for all open pages of the supplied windows. */
	private void addPageListener(IWorkbenchWindow window) {
		// for new pages added in this window
		pageListener = new PageListener(userActionManager);
		window.addPageListener(pageListener);

		// for existing pages in this window
		for (IWorkbenchPage page : window.getPages()) {
			pageListener.pageOpened(page);
		}
	}

	private void addPerspectiveListener(IWorkbenchWindow window) {
		// TODO (MMB) Add listener for debug perspective
	}
}
