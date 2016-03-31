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
	BREAKPOINT_REMOVE,
	
	/** The debug target is suspended due to a breakpoint hit. */
	@SerializedName("sb")
	SUSPEND_BREAKPOINT,
	
	/** The debug target is suspended due to a call to suspend(). */
	@SerializedName("sc")
	SUSPEND_CLIENT,
	
	/** A step out event is performed by the user. */
	@SerializedName("st")
	STEP_OUT,

	/** A step into event is performed by the user. */
	@SerializedName("si")
	STEP_INTO,	

	/** A step over event is performed by the user. */
	@SerializedName("so")
	STEP_OVER,
	
	/** The program is resumed by the user. */
	@SerializedName("rc")
	RESUME_CLIENT,
	
	/** The user executes the 'Inspect' command. */
	@SerializedName("iv")
	INSPECT_VARIABLE,
	
	/** The user defined a watch expression. */
	@SerializedName("dw")
	DEFINE_WATCH,
	
	/** The user opened the expressions dialog (IntelliJ only). */
	@SerializedName("ee")
	EVALUATE_EXPRESSION,
	
	/** The user changed the value of a variable. */
	@SerializedName("mvv")
	MODIFY_VARIABLE_VALUE;

}
