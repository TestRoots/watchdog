package nl.tudelft.watchdog.logic.interval.intervaltypes;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration depicting the different possible activities a developer can
 * perform.
 */
public enum IntervalType {
	/** Users types in the IDE. */
	@SerializedName("ty")
	TYPING,

	/** User reads, ie. no key strokes detectable. */
	@SerializedName("re")
	READING,

	/** EclipseOpen. */
	@SerializedName("eo")
	ECLIPSE_OPEN,

	/** EclipseOpen. */
	@SerializedName("ea")
	ECLIPSE_ACTIVE,

	/** A Perspective interval. */
	@SerializedName("pe")
	PERSPECTIVE;

}
