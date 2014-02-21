package nl.tudelft.watchdog.exceptions;

@SuppressWarnings("serial")
public class ContentReaderException extends Exception {
	public ContentReaderException() {
		super();
	}

	public ContentReaderException(String message) {
		super(message);
	}
}
