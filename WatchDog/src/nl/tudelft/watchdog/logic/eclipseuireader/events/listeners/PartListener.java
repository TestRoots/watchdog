package nl.tudelft.watchdog.logic.eclipseuireader.events.listeners;

import nl.tudelft.watchdog.logic.eclipseuireader.DocumentChangeListenerAttacher;
import nl.tudelft.watchdog.logic.eclipseuireader.events.ImmediateNotifyingObservable;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusEndEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StopEditingEditorEvent;
import nl.tudelft.watchdog.logic.interval.IntervalManager;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A listener on parts. */
public class PartListener implements IPartListener {

	/** Constructor. */
	public PartListener() {
		editorObservable = IntervalManager.getInstance().getEditorObserveable();
	}

	/** The eventObservable. */
	private ImmediateNotifyingObservable editorObservable;

	@Override
	public void partOpened(IWorkbenchPart part) {
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {

			editorObservable.notifyObservers(new FocusEndEditorEvent(part));
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			editorObservable.notifyObservers(new StopEditingEditorEvent(part));
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			editorObservable.notifyObservers(new FocusStartEditorEvent(part));
			DocumentChangeListenerAttacher.listenToDocumentChanges(part);
		}
	}

}
