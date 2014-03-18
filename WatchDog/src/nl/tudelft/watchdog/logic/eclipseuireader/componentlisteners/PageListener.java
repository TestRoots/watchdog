package nl.tudelft.watchdog.logic.eclipseuireader.componentlisteners;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;

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

	static void addPartListener(IWorkbenchPage page) {
		// for new added parts
		page.addPartListener(new PartListener());
	}
}