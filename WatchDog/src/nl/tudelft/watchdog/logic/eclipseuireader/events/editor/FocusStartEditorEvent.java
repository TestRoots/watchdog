package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

public class FocusStartEditorEvent extends EditorEvent {

	public FocusStartEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
