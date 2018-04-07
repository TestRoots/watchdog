package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;

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

	/** The {@link EditorWrapperBase} associated with this interval. */
	protected transient EditorWrapperBase editor;

	/**
	 * To optimize performance, closing of {@link EditorIntervalBase} intervals
	 * is done in their own separate thread.
	 */
	protected transient EditorIntervalCloser editorIntervalCloser;

	/** Constructor. */
	public EditorIntervalBase(EditorWrapperBase editor, IntervalType activity,
			Date start) {
		super(activity, start);
		this.editor = editor;
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

	/** @return The {@link EditorWrapperBase} associated with this interval. */
	public EditorWrapperBase getEditorWrapper() {
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
