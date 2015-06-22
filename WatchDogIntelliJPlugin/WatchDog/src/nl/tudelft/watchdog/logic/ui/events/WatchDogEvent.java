package nl.tudelft.watchdog.logic.ui.events;

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
	private final EventType type;

	/** The different type of events. */
	@SuppressWarnings("javadoc")
	public enum EventType {
		ACTIVE_FOCUS, INACTIVE_FOCUS, SUBSEQUENT_EDIT, START_EDIT, CARET_MOVED, PAINT,

		ACTIVE_WINDOW, INACTIVE_WINDOW, START_INTELLIJ, END_INTELLIJ,

		JUNIT,

		USER_ACTIVITY, USER_INACTIVITY, TYPING_INACTIVITY, READING_INACTIVITY,

		START_WATCHDOGVIEW, END_WATCHDOGVIEW
	}

	/** @return the {@link nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType} of this event. */
	public EventType getType() {
		return type;
	}

}