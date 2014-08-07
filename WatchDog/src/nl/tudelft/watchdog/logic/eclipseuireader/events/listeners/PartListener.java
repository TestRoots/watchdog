package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.events.EditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.EventManager;
import nl.tudelft.watchdog.logic.eclipseuireader.events.WatchDogEvent.EventType;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A listener on parts. Eclipse parts can be views or editors. We are only
 * interested in parts that are ITextEditors.
 */
public class PartListener implements IPartListener {

	/** Constructor. */
	public PartListener(EventManager userActionManager) {
		this.eventManager = userActionManager;
	}

	/** The eventObservable. */
	private EventManager eventManager;

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) part;
			new EditorListener(eventManager, editor);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// TODO (MMB) extract instanceof check outside of PartListener?
		if (part instanceof ITextEditor) {
			eventManager.update(new EditorEvent(part, EventType.END_FOCUS));
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			eventManager.update(new EditorEvent(part, EventType.ACTIVE_FOCUS));
		}
	}

}
