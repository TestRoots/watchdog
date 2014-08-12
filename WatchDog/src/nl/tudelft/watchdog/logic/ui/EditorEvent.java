package nl.tudelft.watchdog.logic.ui;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** An Editor event. */
public class EditorEvent extends WatchDogEvent {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public EditorEvent(IWorkbenchPart part, EventType type) {
		super(part, type);
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
