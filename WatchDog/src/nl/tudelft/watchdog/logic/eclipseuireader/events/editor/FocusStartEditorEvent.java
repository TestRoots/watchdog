package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

/** Event describing a start of focus in the editor. */
public class FocusStartEditorEvent extends EditorEvent {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public FocusStartEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
