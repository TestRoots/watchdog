package nl.tudelft.watchdog.core.logic.breakpoint;

import com.google.gson.annotations.SerializedName;

/** The different types a breakpoint may have. */
public enum BreakpointType {
	/** A line breakpoint. */
	@SerializedName("li")
	LINE,
	
	/** An exception breakpoint. */
	@SerializedName("ex")
	EXCEPTION,
	
	/** A field breakpoint. */
	@SerializedName("fi")
	FIELD,
	
	/** A method breakpoint. */
	@SerializedName("me")
	METHOD,
	
	/** A conditional breakpoint with a specified expression. */
	@SerializedName("ce")
	COND_EXPR,
	
	/** A conditional breakpoint with a hit/pass count. */
	@SerializedName("ch")
	COND_HIT;
}
