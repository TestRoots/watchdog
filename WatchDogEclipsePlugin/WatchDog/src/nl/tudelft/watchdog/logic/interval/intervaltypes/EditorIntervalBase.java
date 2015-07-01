package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.document.Document;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.gson.annotations.SerializedName;

/**
 * A base class for editor intervals. Subclasses of this class should examine
 * whether subclassing {@link EditorIntervalCloser} and then overriding
 * {@link #createIntervalCloser()} makes sense.
 */
public abstract class EditorIntervalBase extends IntervalBase {

	/**
	 * The operations that need to be carried out to close this interval.
	 * Subclasses can contribute their own closing behavior by extending this
	 * class, but should first call super.run().
	 */
	class EditorIntervalCloser implements Runnable {
		@Override
		public void run() {
			if (document != null) {
				document.prepareDocument();
			}
			isClosed = true;
		}
	}

	/** Serialized version. */
	private static final long serialVersionUID = 4L;

	/** The document associated with this {@link EditorIntervalBase}. */
	@SerializedName("doc")
	private Document document;

	/** The {@link ITextEditor} associated with this interval. */
	protected transient ITextEditor editor;

	/*** The {@link IWorkbenchPart} associated with this interval. */
	protected transient IWorkbenchPart part;

	/**
	 * To optimize performance, closing of {@link EditorIntervalBase} intervals
	 * is done in their own separate thread.
	 */
	protected transient EditorIntervalCloser editorIntervalCloser;

	/** Constructor. */
	public EditorIntervalBase(ITextEditor editor, IntervalType activity,
			Date start) {
		super(activity, start);
		this.part = editor;
		this.editor = (ITextEditor) editor;
		this.editorIntervalCloser = createIntervalCloser();
	}

	/**
	 * Creates and returns an {@link EditorIntervalBase}. This method is
	 * automatically invoked by the constructor to setup the closer. Subclasses
	 * implementing their own closing behavior should therefore also override
	 * this method.
	 */
	protected EditorIntervalCloser createIntervalCloser() {
		return new EditorIntervalCloser();
	}

	/** @return The {@link IWorkbenchPart} associated with this interval. */
	public IWorkbenchPart getPart() {
		return part;
	}

	/** @return The {@link ITextEditor} associated with this interval. */
	public ITextEditor getEditor() {
		return editor;
	}

	/** @return the document the interval is associated with. */
	public Document getDocument() {
		return document;
	}

	/** Sets the document. */
	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public void close() {
		super.close();
		isClosed = false;
		new Thread(editorIntervalCloser).start();
	}

}