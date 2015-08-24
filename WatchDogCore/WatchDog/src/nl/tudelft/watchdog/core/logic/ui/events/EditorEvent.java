package nl.tudelft.watchdog.core.logic.ui.events;


/** An Editor event. */
public class EditorEvent extends WatchDogEvent {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public EditorEvent(Object editor, EventType type) {
		super(editor, type);
	}
}
