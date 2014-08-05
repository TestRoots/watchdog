package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import org.eclipse.ui.IWorkbenchPart;

/**
 * @author mbeller
 *
 */
public class FocusEndEditorEvent extends EditorEvent {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public FocusEndEditorEvent(IWorkbenchPart part) {
		super(part);
	}

}
