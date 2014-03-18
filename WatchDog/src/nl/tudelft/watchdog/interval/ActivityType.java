package nl.tudelft.watchdog.interval;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration depicting the different possible activities a developer can
 * perform.
 */
public enum ActivityType {
	/** Users types in the IDE. */
	@SerializedName("ty")
	Typing,

	/** User reads, ie. no key strokes detectable. */
	@SerializedName("re")
	Reading,

	/** Neither typing nor reading. */
	@SerializedName("un")
	Unknown
}
