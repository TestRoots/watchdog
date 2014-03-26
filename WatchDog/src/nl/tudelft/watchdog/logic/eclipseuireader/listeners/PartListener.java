package nl.tudelft.watchdog.logic.eclipseuireader.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.DocumentChangeListenerAttacher;
import nl.tudelft.watchdog.logic.eclipseuireader.events.EditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentNotifier;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A listener on parts. */
public class PartListener implements IPartListener {
	@Override
	public void partOpened(IWorkbenchPart part) {

	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			DocumentNotifier
					.fireDocumentEndFocusEvent(new EditorEvent(
							part));
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			DocumentNotifier
					.fireDocumentStopEditingEvent(new EditorEvent(
							part));
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			DocumentNotifier
					.fireDocumentStartFocusEvent(new EditorEvent(
							part));
			DocumentChangeListenerAttacher.listenToDocumentChanges(part);
		}
	}

}
