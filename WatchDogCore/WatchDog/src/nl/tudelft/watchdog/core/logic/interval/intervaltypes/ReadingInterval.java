package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;

/** A reading interval, i.e. an interval in which the user read some code. */
public class ReadingInterval extends EditorIntervalBase {
	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ReadingInterval(EditorWrapperBase editor, Date start) {
		super(editor, IntervalType.READING, start);
	}

}
