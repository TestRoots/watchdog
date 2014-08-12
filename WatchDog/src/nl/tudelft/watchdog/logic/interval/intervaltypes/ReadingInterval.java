package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Timer;

import org.eclipse.ui.IWorkbenchPart;

/** A reading interval, i.e. an interval in which the user read some code. */
public class ReadingInterval extends UserActivityIntervalBase {
	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ReadingInterval(IWorkbenchPart part, long sessionSeed) {
		super(part, IntervalType.READING, sessionSeed);
		timer = new Timer();
	}

}
