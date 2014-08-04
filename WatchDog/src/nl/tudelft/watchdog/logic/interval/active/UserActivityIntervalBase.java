package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.interval.activityCheckers.CheckerTimerTask;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.gson.annotations.SerializedName;

/** A base class for */
public abstract class UserActivityIntervalBase extends IntervalBase {

	/** Serialized version. */
	private static final long serialVersionUID = 2L;

	/** The document associated with this {@link RecordedInterval}. */
	@SerializedName("doc")
	private Document document;

	/** The typing task. */
	protected transient CheckerTimerTask task;

	/** The {@link ITextEditor} associated with this interval. */
	protected transient ITextEditor editor;

	/*** The {@link IWorkbenchPart} associated with this interval. */
	protected transient IWorkbenchPart part;

	/** Constructor. */
	public UserActivityIntervalBase(IWorkbenchPart part, IntervalType activity,
			long sessionSeed) {
		super(activity, sessionSeed);
		this.part = part;
		this.editor = (ITextEditor) part;
	}

	@Override
	public void listenForReactivation() {
		// TODO (MMB) task would be null if addTimeoutListener had not been
		// called before listenForReactivation ?
		assert (task != null);
		task.createListenerForReactivation();
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