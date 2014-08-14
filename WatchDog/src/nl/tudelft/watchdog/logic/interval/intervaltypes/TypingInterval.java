package nl.tudelft.watchdog.logic.interval.intervaltypes;

import org.eclipse.ui.texteditor.ITextEditor;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#TYPING} activity.
 */
public class TypingInterval extends EditorIntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public TypingInterval(ITextEditor part) {
		super(part, IntervalType.TYPING);
	}

	@Override
	public IntervalType getType() {
		return IntervalType.TYPING;
	}
}
