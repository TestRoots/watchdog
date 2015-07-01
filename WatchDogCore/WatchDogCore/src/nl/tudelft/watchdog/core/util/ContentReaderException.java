package nl.tudelft.watchdog.core.util;

/** Exception thrown if the contents of an editor could not be read. */
public class ContentReaderException extends Exception {
	/** Version. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ContentReaderException(String message) {
		super(message);
	}
}
