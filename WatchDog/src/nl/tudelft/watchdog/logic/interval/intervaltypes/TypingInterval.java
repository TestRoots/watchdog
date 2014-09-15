package nl.tudelft.watchdog.logic.interval.intervaltypes;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.network.JsonifiedLong;

import org.eclipse.ui.texteditor.ITextEditor;

import com.cedarsoftware.util.StringUtilities;
import com.google.gson.annotations.SerializedName;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#TYPING} activity.
 */
public class TypingInterval extends EditorIntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/**
	 * The document content associated with this {@link TypingInterval} when it
	 * has ended.
	 */
	private Document endingDocument;

	/**
	 * The edit distance performed in this interval, i.e. a metric of the amount
	 * of text that was updated.
	 */
	@SerializedName("diff")
	JsonifiedLong editDistance;

	/** Constructor. */
	public TypingInterval(ITextEditor editor) {
		super(editor, IntervalType.TYPING);
	}

	@Override
	public IntervalType getType() {
		return IntervalType.TYPING;
	}

	/** Updates the contents when ending the typing interval. */
	public void setEndingDocument(Document endingDocument) {
		this.endingDocument = endingDocument;
	}

	@Override
	public void close() {
		super.close();
		if (endingDocument != null) {
			endingDocument.prepareDocument();
		}
		// calculate the Levenshtein distance between the two edit operations.
		if (getDocument() != null && endingDocument != null) {
			String startingContent = getDocument().getContent();
			String endingContent = endingDocument.getContent();
			if (startingContent != null && endingContent != null) {
				editDistance = new JsonifiedLong(
						StringUtilities.levenshteinDistance(startingContent,
								endingContent));
			}
		}
	}
}
