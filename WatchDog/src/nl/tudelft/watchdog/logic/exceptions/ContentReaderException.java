package nl.tudelft.watchdog.logic.exceptions;

@SuppressWarnings("serial")
public class ContentReaderException extends Exception {
	public ContentReaderException() {
		super();
	}

	public ContentReaderException(String message) {
		super(message);
	}
}
