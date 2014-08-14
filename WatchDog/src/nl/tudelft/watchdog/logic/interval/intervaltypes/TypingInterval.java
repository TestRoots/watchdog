package nl.tudelft.watchdog.logic.interval.intervaltypes;

import org.eclipse.ui.texteditor.ITextEditor;

import com.google.gson.annotations.SerializedName;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#TYPING} activity.
 */
public class TypingInterval extends EditorIntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/**
	 * The edit distance performed in this interval, i.e. a metric of the amount
	 * of text that was updated.
	 */
	@SerializedName("diff")
	int editDistance = 0;

	/** Constructor. */
	public TypingInterval(ITextEditor part) {
		super(part, IntervalType.TYPING);
	}

	@Override
	public IntervalType getType() {
		return IntervalType.TYPING;
	}
}
