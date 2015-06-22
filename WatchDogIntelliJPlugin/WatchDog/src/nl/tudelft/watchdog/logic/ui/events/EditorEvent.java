package nl.tudelft.watchdog.logic.ui.events;

import com.intellij.openapi.editor.Editor;

/** An Editor event. */
public class EditorEvent extends WatchDogEvent {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public EditorEvent(Editor editor, EventType type) {
		super(editor, type);
	}

	/** @return The {@link Editor} this event occurred on. */
	public Editor getTextEditor() {
		return (Editor) source;
	}

}
