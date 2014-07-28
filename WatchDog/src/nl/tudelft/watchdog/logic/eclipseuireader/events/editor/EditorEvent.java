package nl.tudelft.watchdog.logic.eclipseuireader.events.editor;

import java.util.EventObject;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** An Editor event. */
public class EditorEvent extends EventObject {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public EditorEvent(IWorkbenchPart part) {
		super(part);
	}

	/** @return The workbench part this event occurred on. */
	public IWorkbenchPart getPart() {
		return (IWorkbenchPart) source;
	}

	/** @return The {@link ITextEditor} this event occurred on. */
	public ITextEditor getTextEditor() {
		return (ITextEditor) source;
	}
}
