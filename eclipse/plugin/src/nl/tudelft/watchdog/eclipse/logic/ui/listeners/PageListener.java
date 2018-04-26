package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;

/** A listener on Pages. */
public class PageListener implements IPageListener {

	private final TrackingEventManager trackingEventManager;

	PageListener(TrackingEventManager trackingEventManager) {
		this.trackingEventManager = trackingEventManager;
	}

	@Override
	public void pageOpened(IWorkbenchPage page) {
		addPartListener(page);
	}

	@Override
	public void pageClosed(IWorkbenchPage page) {
	}

	@Override
	public void pageActivated(IWorkbenchPage page) {
	}

	/** Adds a part listener for newly added parts */
	public void addPartListener(IWorkbenchPage page) {
		final PartListener partListener = new PartListener(trackingEventManager);
		page.addPartListener(partListener);

		// trigger already opened part
		IWorkbenchPart activePart = page.getActiveEditor();
		if (activePart != null) {
			partListener.partOpened(activePart);
		}
	}
}
