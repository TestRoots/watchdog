package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Timer;

import org.eclipse.ui.IWorkbenchPart;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#Typing} activity.
 */
public class TypingInterval extends UserActivityIntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public TypingInterval(IWorkbenchPart part, long sessionSeed) {
		super(part, IntervalType.Typing, sessionSeed);
		timer = new Timer();
	}

	@Override
	public IntervalType getActivityType() {
		return IntervalType.Typing;
	}
}
