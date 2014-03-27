package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;

// TODO (MMB) Causes an exception when run with existing editors in the IDE (unhandled event loop)
/** A listener on Pages. */
public class PageListener implements IPageListener {

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
	static void addPartListener(IWorkbenchPage page) {
		page.addPartListener(new PartListener());
	}
}