package nl.tudelft.watchdog.core.logic.network;

/**
 * Exceptiont to document that the server did not send the expected HTTP return
 * code.
 */
public class ServerReturnCodeException extends Exception {
	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ServerReturnCodeException(String message) {
		super(message);
	}

}
