package nl.tudelft.watchdog.logic.eclipseuireader.componentlisteners;

import nl.tudelft.watchdog.logic.eclipseuireader.DocChangeListenerAttacher;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentActivateOrDeactivateEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentNotifier;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 *
 */
public class PartListener implements IPartListener {
	@Override
	public void partOpened(IWorkbenchPart part) {

	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			DocumentNotifier
					.fireDocumentEndFocusEvent(new DocumentActivateOrDeactivateEvent(
							part));
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			DocumentNotifier
					.fireDocumentStopEditingEvent(new DocumentActivateOrDeactivateEvent(
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
					.fireDocumentStartFocusEvent(new DocumentActivateOrDeactivateEvent(
							part));
			DocChangeListenerAttacher.listenToDocChanges(part);
		}
	}

}
