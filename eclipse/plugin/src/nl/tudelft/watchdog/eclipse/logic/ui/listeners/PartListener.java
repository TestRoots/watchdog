package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.ui.events.EditorEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A listener on parts. Eclipse parts can be views or editors. We are only
 * interested in parts that are ITextEditors.
 */
public class PartListener implements IPartListener {

	/** Constructor. */
	public PartListener() {
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) part;
			new EditorListener(editor);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			new EditorEvent(part, EventType.INACTIVE_FOCUS).update();
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			new EditorEvent(part, EventType.INACTIVE_FOCUS).update();
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			new EditorEvent(part, EventType.ACTIVE_FOCUS).update();
		}
	}

}
