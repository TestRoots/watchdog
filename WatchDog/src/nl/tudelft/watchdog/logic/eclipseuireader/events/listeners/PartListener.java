package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.events.UserActionListenerManager;
import nl.tudelft.watchdog.logic.eclipseuireader.events.UserActionManager;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusEndEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A listener on parts. */
public class PartListener implements IPartListener {

	/** Constructor. */
	public PartListener(UserActionManager userActionManager) {
		this.userActionManager = userActionManager;
	}

	/** The eventObservable. */
	private UserActionManager userActionManager;
	private UserActionListenerManager userActionListener;

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) part;
			userActionListener = new UserActionListenerManager(
					userActionManager, editor);
			// userActionManager.update(new FocusStartEditorEvent(part));
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// TODO (MMB) extract instanceof check outside of PartListener?
		if (part instanceof ITextEditor) {
			userActionManager.update(new FocusEndEditorEvent(part));
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		userActionListener.removeListeners();
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			userActionManager.update(new FocusStartEditorEvent(part));
		}
	}

}
