package nl.tudelft.watchdog.core.logic.breakpoint;

import com.google.gson.annotations.SerializedName;

/** The different types of changes to a breakpoint. */
public enum BreakpointChangeType {
	/** Breakpoint has been enabled. */
	@SerializedName("en")
	ENABLED,

	/** Breakpoint has been disabled. */
	@SerializedName("ds")
	DISABLED,

	/** Hit count added on breakpoint. */
	@SerializedName("ha")
	HC_ADDED,

	/** Breakpoint's hit count has been changed. */
	@SerializedName("hc")
	HC_CHANGED,

	/** Breakpoint's hit count has been removed. */
	@SerializedName("hr")
	HC_REMOVED,

	/** Breakpoint's suspend policy has been changed. */
	@SerializedName("sp")
	SP_CHANGED,

	/** Breakpoint's condition has been enabled. */
	@SerializedName("ce")
	COND_ENABLED,

	/** Breakpoint's condition has been disabled. */
	@SerializedName("cd")
	COND_DISABLED,

	/** Breakpoint's condition has changed. */
	@SerializedName("cc")
	COND_CHANGED,

	/** Unknown change to the breakpoint. */
	@SerializedName("un")
	UNKNOWN;
}
