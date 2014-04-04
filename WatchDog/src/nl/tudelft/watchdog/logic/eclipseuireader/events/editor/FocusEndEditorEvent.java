package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

public class FocusEndEditorEvent extends EditorEvent {

	public FocusEndEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
