package nl.tudelft.watchdog.core.logic.network;

import java.io.Serializable;

/**
 * Wrapper containing a long (not {@link Long}!) that allows lazy Initialization
 * through Json.
 */
public class JsonifiedLong implements Serializable {
	/** Version ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public JsonifiedLong(long value) {
		this.value = value;
	}

	/** The long that is being wrapped. */
	public long value;
}
