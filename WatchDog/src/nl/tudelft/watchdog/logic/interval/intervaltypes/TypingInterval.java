package nl.tudelft.watchdog.logic.interval.intervaltypes;

import org.eclipse.ui.IWorkbenchPart;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#TYPING} activity.
 */
public class TypingInterval extends EditorIntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public TypingInterval(IWorkbenchPart part) {
		super(part, IntervalType.TYPING);
	}

	@Override
	public IntervalType getActivityType() {
		return IntervalType.TYPING;
	}
}
