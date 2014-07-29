package nl.tudelft.watchdog.logic.exceptions;

/** Default exception for server communication problems. */
public class ServerCommunicationException extends Exception {

	/** Constructor. */
	public ServerCommunicationException(String errorMessage) {
		super(errorMessage);
	}

	/** Serial ID. */
	private static final long serialVersionUID = 1L;
}
