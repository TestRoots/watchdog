package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

public class StopEditingEditorEvent extends EditorEvent {

	public StopEditingEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
