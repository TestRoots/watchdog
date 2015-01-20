package nl.tudelft.watchdog.logic.network;

/**
 * Default exception for server communication problems. Normally represents a
 * temporary problem.
 */
public class ServerCommunicationException extends Exception {
	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ServerCommunicationException(String errorMessage) {
		super(errorMessage);
	}
}
