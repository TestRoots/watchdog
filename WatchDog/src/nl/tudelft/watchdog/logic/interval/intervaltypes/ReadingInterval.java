package nl.tudelft.watchdog.logic.interval.intervaltypes;

import org.eclipse.ui.texteditor.ITextEditor;

/** A reading interval, i.e. an interval in which the user read some code. */
public class ReadingInterval extends EditorIntervalBase {
	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ReadingInterval(ITextEditor part) {
		super(part, IntervalType.READING);
	}

}
