package nl.tudelft.watchdog.core.logic.event.eventtypes;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration of the different possible events that can be fired based on the
 * activities a developer can perform.
 */
public enum EventType {
	/** User adds a breakpoint. */
	@SerializedName("ba")
	BREAKPOINT_ADD,
	
	/** User changes a breakpoint. */
	@SerializedName("bc")
	BREAKPOINT_CHANGE,
	
	/** User removes a breakpoint. */
	@SerializedName("br")
	BREAKPOINT_REMOVE;

}
