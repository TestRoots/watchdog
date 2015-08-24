package nl.tudelft.watchdog.core.logic.ui.events;

/** Event that is thrown when a JUnit execution occurred. */
public class JUnitEvent extends WatchDogEvent {

	/** Constructor. */
	public JUnitEvent(Object source) {
		super(source, EventType.JUNIT);
	}

	/** Serialized version id. */
	private static final long serialVersionUID = 1L;

}
