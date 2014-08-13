package nl.tudelft.watchdog.logic.interval.intervaltypes;

import org.eclipse.ui.IWorkbenchPart;

/** A reading interval, i.e. an interval in which the user read some code. */
public class ReadingInterval extends EditorIntervalBase {
	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ReadingInterval(IWorkbenchPart part) {
		super(part, IntervalType.READING);
	}

}
