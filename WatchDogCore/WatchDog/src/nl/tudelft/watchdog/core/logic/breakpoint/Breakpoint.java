package nl.tudelft.watchdog.core.logic.breakpoint;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Data container which stores information about a breakpoint, including its
 *  {@link BreakpointType}.
 */
public class Breakpoint implements Serializable {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** The hash of this breakpoint. */
	@SerializedName("bh")
	private String hash;
	
	/** The type of this breakpoint. */
	@SerializedName("bt")
	private BreakpointType breakpointType;
	
	/** Constructor. */
	public Breakpoint(BreakpointType type, String hash) {
		this.breakpointType = type;
		this.hash = hash;
	}

	/** @return the hash of the breakpoint. */
	public String getHash() {
		return hash;
	}

	/** Sets the hash of the breakpoint. */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/** @return the type of the breakpoint. */
	public BreakpointType getBreakpointType() {
		return breakpointType;
	}

	/** Sets the type of the breakpoint. */
	public void setBreakpointType(BreakpointType breakpointType) {
		this.breakpointType = breakpointType;
	}
}
