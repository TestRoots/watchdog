package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.interval.activityCheckers.CheckerTimerTask;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A base class for */
public abstract class UserActivityIntervalBase extends IntervalBase {

	/** The typing task. */
	protected transient CheckerTimerTask task;

	/** The {@link ITextEditor} associated with this interval. */
	protected transient ITextEditor editor;

	/*** The {@link IWorkbenchPart} associated with this interval. */
	protected transient IWorkbenchPart part;

	/** Constructor. */
	public UserActivityIntervalBase(IWorkbenchPart part, IntervalType activity,
			String userid, long sessionSeed) {
		super(activity, userid, sessionSeed);
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