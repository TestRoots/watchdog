package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.events.ImmediateNotifyingObservable;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;

/** A listener on Pages. */
public class PageListener implements IPageListener {

	/** The editorObservable. */
	private ImmediateNotifyingObservable editorObservable;

	/** Constructor. */
	public PageListener(ImmediateNotifyingObservable editorObservable) {
		this.editorObservable = editorObservable;
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
		page.addPartListener(new PartListener(editorObservable));
	}
}