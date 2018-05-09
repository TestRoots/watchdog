package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

/**
 * Internal representation of a Static Analysis warning. It is used both as serializable format
 * to send to the database, as well as an internal storage unit in the IntelliJ implementation.
 * Therefore, {@link #type} is of generic type, which in practice is only a {@link String} or
 * a RangeHighlighter in IntelliJ.
 *
 * @param <T> Either a {@link String} or a RangeHighlighter in IntelliJ.
 */
public class Warning<T> implements Serializable {

	private static final long serialVersionUID = -573132089990619360L;

	@SerializedName("doctotal")
	public final int docTotalLines;

	@SerializedName("type")
	public final T type;

	@SerializedName("line")
	public final int lineNumber;

	@SerializedName("time")
	public final Date warningCreationTime;

	@SerializedName("diff")
	public final int secondsBetween;

	/**
	 * Create a warning in a document, without a difference in time of a previous warning event
	 * (in the case this event is a warning deletion).
	 * @param docTotalLines The total number of lines in the document at the moment the warning was created.
	 * @param type The type of warning, could be a highlighter or the textual classification of the warning.
	 * @param lineNumber The line number this warning was created on.
	 * @param warningCreationTime The timestamp this warning was created at.
	 */
	public Warning(int docTotalLines, T type, int lineNumber, Date warningCreationTime) {
		this(docTotalLines, type, lineNumber, warningCreationTime, -1);
	}

	/**
	 * Create a warning in a document, including a difference in time of a previous warning event
	 * (in the case this event is a warning deletion).
	 * @param docTotalLines The total number of lines in the document at the moment the warning was created.
	 * @param type The type of warning, could be a highlighter or the textual classification of the warning.
	 * @param lineNumber The line number this warning was created on.
	 * @param warningCreationTime The timestamp this warning was created at.
	 * @param secondsBetween The number of seconds between a warning deletion and the original warning creation timestamp.
	 */
	public Warning(int docTotalLines, T type, int lineNumber, Date warningCreationTime, int secondsBetween) {
		this.docTotalLines = docTotalLines;
		this.type = type;
		this.warningCreationTime = warningCreationTime;
		this.secondsBetween = secondsBetween;
		this.lineNumber = lineNumber;
	}
}
