package nl.tudelft.watchdog.logic.interval.active;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration depicting the different possible activities a developer can
 * perform.
 */
public enum IntervalType {
	/** Users types in the IDE. */
	@SerializedName("ty")
	Typing,

	/** User reads, ie. no key strokes detectable. */
	@SerializedName("re")
	Reading,

	/** EclipseOpen. */
	@SerializedName("se")
	Session;

	/** Return the JSON field name from each enum value */
	public String jsonFieldName() {
		switch (this.ordinal()) {
		case 1:
			return "ty";
		case 2:
			return "re";
		case 3:
			return "se";
		default:
			return null;
		}
	}

	/** Constructs an IntervalType from its JSON mnemonic */
	public static IntervalType fromMnemonic(String mnem) {
		switch (mnem) {
		case "ty":
			return IntervalType.Typing;
		case "re":
			return IntervalType.Reading;
		case "se":
			return IntervalType.Session;
		default:
			return null;
		}
	}
}
