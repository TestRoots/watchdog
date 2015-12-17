package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;
import nl.tudelft.watchdog.core.logic.network.JsonifiedLong;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#TYPING} activity.
 */
public class TypingInterval extends EditorIntervalBase {

	/** The operations that need to be carried out to close this interval. */
	private class TypingIntervalCloserBase extends EditorIntervalCloser {
		@Override
		public void run() {
			super.run();
			isClosed = false;

			if (endingDocument != null) {
				endingDocument.prepareDocument();
			}
			// calculate the Levenshtein distance between the two edit
			// operations.
			if (getDocument() != null && endingDocument != null) {
				String startingContent = getDocument().getContent();
				String endingContent = endingDocument.getContent();
				if (startingContent != null && endingContent != null) {
					editDistance = new JsonifiedLong(
							StringUtils.getLevenshteinDistance(startingContent, endingContent));
				}
			}
			isClosed = true;
		}
	}

	/** Serial ID. */
	private static final long serialVersionUID = 2L;

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
	
	/**
	 * The number of characters that have been added, removed or modified during
	 * this interval.
	 */	
	@SerializedName("modCount")
	private long modCount;

	/** Constructor. */
	public TypingInterval(EditorWrapperBase editor, Date start) {
		super(editor, IntervalType.TYPING, start);
		this.modCount = 0;
	}

	@Override
	public IntervalType getType() {
		return IntervalType.TYPING;
	}

	/** Updates the contents when ending the typing interval. */
	public void setEndingDocument(Document endingDocument) {
		this.endingDocument = endingDocument;
	}
	
	/** Increases the number of characters modified in this interval. */
	public void increaseModCountWith(long nrOfModifiedChars) {
		if(nrOfModifiedChars > 0) {
			this.modCount += nrOfModifiedChars;
		}
	}

	@Override
	protected EditorIntervalCloser createIntervalCloser() {
		return new TypingIntervalCloserBase();
	}

}
