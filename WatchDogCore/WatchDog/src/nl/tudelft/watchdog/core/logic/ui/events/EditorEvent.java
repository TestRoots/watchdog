package nl.tudelft.watchdog.core.logic.ui.events;


/** An Editor event. */
public class EditorEvent extends WatchDogEvent {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;
	
	private long modifiedChars;

	/** Constructor. */
	public EditorEvent(Object editor, EventType type) {
		super(editor, type);
		modifiedChars = 0L;
	}
	
	/** Sets the number of characters that have been modified by this event. */
	public void setModCount(long nrOfModifiedChars) {
		this.modifiedChars = nrOfModifiedChars;
	}
	
	/** Gets the number of characters that have been modified by this event. */
	public long getModCount() {
		return this.modifiedChars;
	}
	
}
