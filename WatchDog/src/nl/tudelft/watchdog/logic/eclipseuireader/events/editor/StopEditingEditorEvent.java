package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

/** Event describing a stop of editing in the editor. */
public class StopEditingEditorEvent extends EditorEvent {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public StopEditingEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
