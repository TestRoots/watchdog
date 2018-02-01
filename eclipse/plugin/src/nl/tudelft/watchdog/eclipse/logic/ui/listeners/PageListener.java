package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/** A listener on Pages. */
public class PageListener implements IPageListener {

	/** Constructor. */
	public PageListener() {
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
		final PartListener partListener = new PartListener();
		page.addPartListener(partListener);

		// trigger already opened part
		IWorkbenchPart activePart = page.getActiveEditor();
		if (activePart != null) {
			partListener.partOpened(activePart);
		}
	}
}