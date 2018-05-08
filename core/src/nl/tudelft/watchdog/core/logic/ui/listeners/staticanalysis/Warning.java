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

	@SerializedName("docline")
	public final int docLineNumber;

	@SerializedName("type")
	public final T type;

	@SerializedName("line")
	public final int lineNumber;

	@SerializedName("time")
	public final Date warningCreationTime;

	@SerializedName("diff")
	public final int secondsBetween;

	public Warning(int docLineNumber, T type, int lineNumber, Date warningCreationTime) {
		this(docLineNumber, type, lineNumber, warningCreationTime, -1);
	}

	public Warning(int docLineNumber, T type, int lineNumber, Date warningCreationTime, int secondsBetween) {
		this.docLineNumber = docLineNumber;
		this.type = type;
		this.warningCreationTime = warningCreationTime;
		this.secondsBetween = secondsBetween;
		this.lineNumber = lineNumber;
	}
}
