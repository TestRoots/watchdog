package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A listener on parts. Eclipse parts can be views or editors. We are only
 * interested in parts that are ITextEditors.
 */
public class PartListener implements IPartListener {

	private WorkbenchListener workbenchListener;

	public PartListener(WorkbenchListener workbenchListener) {
		this.workbenchListener = workbenchListener;
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) part;
			new EditorListener(editor);
			this.workbenchListener.editorCreated(editor);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			WatchDogEventType.INACTIVE_FOCUS.process(part);
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			WatchDogEventType.INACTIVE_FOCUS.process(part);
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			WatchDogEventType.ACTIVE_FOCUS.process(part);
		}
	}

}
