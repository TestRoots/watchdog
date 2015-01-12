package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.logic.ui.EventManager;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/** A listener on Pages. */
public class PageListener implements IPageListener {

	/** The editorObservable. */
	private EventManager userActionManager;

	/** Constructor. */
	public PageListener(EventManager editorObservable) {
		this.userActionManager = editorObservable;
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
		final PartListener partListener = new PartListener(userActionManager);
		page.addPartListener(partListener);

		// trigger already opened part
		IWorkbenchPart activePart = page.getActiveEditor();
		if (activePart != null) {
			partListener.partOpened(activePart);
		}
	}
}