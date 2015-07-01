package nl.tudelft.watchdog.core.logic.network;

import java.io.Serializable;

/**
 * Wrapper containing a double (not {@link Double}!) that allows lazy
 * Initialization through Json.
 */
public class JsonifiedDouble implements Serializable {
	/** Version ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public JsonifiedDouble(double value) {
		this.value = value;
	}

	/** The double that is being wrapped. */
	public double value;
}
