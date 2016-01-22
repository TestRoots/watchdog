package nl.tudelft.watchdog.core.logic.breakpoint;

import com.google.gson.annotations.SerializedName;

/** The different types of changes to a breakpoint. */
public enum BreakpointChangeType {
	/** Breakpoint is enabled. */
	@SerializedName("en")
	ENABLED,

	/** Breakpoint is disabled. */
	@SerializedName("ds")
	DISABLED,
	
	/** Unknown change to the breakpoint. */
	@SerializedName("un")
	UNKNOWN;
}
