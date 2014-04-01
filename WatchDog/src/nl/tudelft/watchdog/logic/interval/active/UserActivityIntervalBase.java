package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.interval.activityCheckers.CheckerTimerTask;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A base class for */
public abstract class UserActivityIntervalBase extends IntervalBase {

	/** The serialization version ID. */
	private static final long serialVersionUID = 1L;

	/** The typing task. */
	protected CheckerTimerTask task;

	/** The {@link ITextEditor} associated with this interval. */
	protected ITextEditor editor;

	/*** The {@link IWorkbenchPart} associated with this interval. */
	protected IWorkbenchPart part;

	/** Constructor. */
	public UserActivityIntervalBase(IWorkbenchPart part) {
		super();
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
}