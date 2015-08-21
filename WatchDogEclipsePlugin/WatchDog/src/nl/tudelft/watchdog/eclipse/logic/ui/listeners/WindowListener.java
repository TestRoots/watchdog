package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.eclipse.logic.ui.EventManager;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

/** Listening for UI event on Eclipse windows. */
public class WindowListener implements IWindowListener {

	/** The eventObservable. */
	private EventManager eventManager;
	private PageListener pageListener;

	/** Constructor. */
	public WindowListener(EventManager userActionManager) {
		this.eventManager = userActionManager;
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		addPageListener(window);
		addPerspectiveListener(window);
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		eventManager
				.update(new WatchDogEvent(window, EventType.INACTIVE_WINDOW));

	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		// TODO (MMB) find a place where it's safe to remove the listeners?
		// window.removePageListener(pageListener);
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		eventManager.update(new WatchDogEvent(window, EventType.ACTIVE_WINDOW));
	}

	/** Adds page listeners for all open pages of the supplied windows. */
	private void addPageListener(IWorkbenchWindow window) {
		// for new pages added in this window
		pageListener = new PageListener(eventManager);
		window.addPageListener(pageListener);

		// for existing pages in this window
		for (IWorkbenchPage page : window.getPages()) {
			pageListener.pageOpened(page);
		}
	}

	private void addPerspectiveListener(IWorkbenchWindow window) {
		IPerspectiveListener perspectiveListener = new PerspectiveListener(
				eventManager);
		window.addPerspectiveListener(perspectiveListener);

		// triggers the event for the currently open perspective, if there is
		// any
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage != null) {
			IPerspectiveDescriptor currentPerspective = window.getActivePage()
					.getPerspective();
			if (currentPerspective != null) {
				perspectiveListener.perspectiveActivated(activePage,
						currentPerspective);
			}

		}
	}
}
