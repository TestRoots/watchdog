package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

public class StartEditingEditorEvent extends EditorEvent {

	public StartEditingEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
