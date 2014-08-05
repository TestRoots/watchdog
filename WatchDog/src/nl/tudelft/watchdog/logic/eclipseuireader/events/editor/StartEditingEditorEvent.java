package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

/** Event describing a start of editing in the editor. */
public class StartEditingEditorEvent extends EditorEvent {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public StartEditingEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
