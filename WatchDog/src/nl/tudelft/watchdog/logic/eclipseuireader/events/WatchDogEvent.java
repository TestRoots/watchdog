package nl.tudelft.watchdog.logic.eclipseuireader.events;

import java.util.EventObject;

/** Any event transferred by WatchDog. */
public class WatchDogEvent extends EventObject {

	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public WatchDogEvent(Object source, EventType type) {
		super(source);
		this.type = type;
	}

	/** The type of the event. */
	protected EventType type;

	/** The different type of events. */
	@SuppressWarnings("javadoc")
	public enum EventType {
		ACTIVE_FOCUS, END_FOCUS, EDIT, ACTIVE_WINDOW, END_WINDOW, CARET_MOVED, PAINT, START_ECLIPSE, END_ECLIPSE,
	}

	/** @return the {@link EventType} of this event. */
	public EventType getType() {
		return type;
	}

}