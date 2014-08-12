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
	ECLIPSE_ACTIVE;

	/** Constructs an IntervalType from its JSON mnemonic */
	public static IntervalType fromMnemonic(String mnem) {
		switch (mnem) {
		case "ty":
			return IntervalType.TYPING;
		case "re":
			return IntervalType.READING;
		case "se":
			return IntervalType.ECLIPSE_ACTIVE;
		default:
			return null;
		}
	}
}
