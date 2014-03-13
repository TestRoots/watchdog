package nl.tudelft.watchdog.interval;

/**
 * Enumeration depicting the different possible activities a developer can
 * perform.
 */
public enum ActivityType {
	/** Users types in the IDE. */
	Typing,

	/** User reads, ie. no key strokes detectable. */
	Reading,

	/** Neither typing nor reading. */
	Unknown
}
