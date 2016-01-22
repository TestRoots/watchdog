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
	private int hash;
	
	/** The type of this breakpoint. */
	@SerializedName("bt")
	private BreakpointType breakpointType;
	
	private boolean enabled;
	
	/** Constructor. */
	public Breakpoint(BreakpointType type, int hash) {
		this.breakpointType = type;
		this.hash = hash;
	}

	/** @return the hash of the breakpoint. */
	public int getHash() {
		return hash;
	}

	/** Sets the hash of the breakpoint. */
	public void setHash(int hash) {
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
	
	/** @return true if the breakpoint is enabled. */
	public boolean isEnabled() {
		return enabled;
	}

	/** Sets whether the breakpoint is enabled or not. */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
