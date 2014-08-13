package nl.tudelft.watchdog.logic.interval.intervaltypes;

import nl.tudelft.watchdog.logic.document.Document;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.gson.annotations.SerializedName;

/** A base class for */
public abstract class EditorIntervalBase extends IntervalBase {

	/** Serialized version. */
	private static final long serialVersionUID = 3L;

	/** The document associated with this {@link RecordedInterval}. */
	@SerializedName("doc")
	private Document document;

	/** The {@link ITextEditor} associated with this interval. */
	protected transient ITextEditor editor;

	/*** The {@link IWorkbenchPart} associated with this interval. */
	protected transient IWorkbenchPart part;

	/** Constructor. */
	public EditorIntervalBase(ITextEditor editor, IntervalType activity) {
		super(activity);
		this.part = editor;
		this.editor = (ITextEditor) editor;
	}

	/** @return The {@link ITextEditor} associated with this interval. */
	public IWorkbenchPart getPart() {
		return part;
	}

	/** @return The {@link IWorkbenchPart} associated with this interval. */
	public ITextEditor getEditor() {
		return editor;
	}

	/**
	 * @return the document the interval is associated with.
	 */
	public Document getDocument() {
		return document;
	}

	/** Sets the document. */
	public void setDocument(Document document) {
		this.document = document;
	}

}