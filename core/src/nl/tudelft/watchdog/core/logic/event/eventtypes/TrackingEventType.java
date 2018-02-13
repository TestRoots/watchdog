package nl.tudelft.watchdog.core.logic.event.eventtypes;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration of the different possible debug events that can be fired based on the
 * activities a developer can perform.
 */
public enum TrackingEventType {
	/** User adds a breakpoint. */
	@SerializedName("ba")
	BREAKPOINT_ADD("Breakpoint Added"),
	
	/** User changes a breakpoint. */
	@SerializedName("bc")
	BREAKPOINT_CHANGE("Breakpoint Changed"),
	
	/** User removes a breakpoint. */
	@SerializedName("br")
	BREAKPOINT_REMOVE("Breakpoint Removed"),
	
	/** The debug target is suspended due to a breakpoint hit. */
	@SerializedName("sb")
	SUSPEND_BREAKPOINT("Suspended (breakpoint)"),
	
	/** The debug target is suspended due to a call to suspend(). */
	@SerializedName("sc")
	SUSPEND_CLIENT("Suspended (client)"),
	
	/** A step out event is performed by the user. */
	@SerializedName("st")
	STEP_OUT("Stepped Out"),

	/** A step into event is performed by the user. */
	@SerializedName("si")
	STEP_INTO("Stepped Into"),

	/** A step over event is performed by the user. */
	@SerializedName("so")
	STEP_OVER("Stepped Over"),
	
	/** The program is resumed by the user. */
	@SerializedName("rc")
	RESUME_CLIENT("Resumed (client)"),
	
	/** The user executes the 'Inspect' command. */
	@SerializedName("iv")
	INSPECT_VARIABLE("Inspected Variable"),
	
	/** The user defined a watch expression. */
	@SerializedName("dw")
	DEFINE_WATCH("Defined Watch"),
	
	/** The user opened the expressions dialog (IntelliJ only). */
	@SerializedName("ee")
	EVALUATE_EXPRESSION("Evaluated Expression"),
	
	/** The user changed the value of a variable. */
	@SerializedName("mv")
	MODIFY_VARIABLE_VALUE("Modified Variable Value"),

	/** A new static analysis warning is generated. */
	@SerializedName("sa-wc")
	SA_WARNING_CREATED("Static analysis warning created"),

	/** A static analysis warning is removed. */
	@SerializedName("sa-wr")
	SA_WARNING_REMOVED("Static analysis warning resolved");

	private final String textualDescription;

	TrackingEventType(String textualDescription) {
		this.textualDescription = textualDescription;
	}

    public String getTextualDescription() {
        return textualDescription;
    }
}
