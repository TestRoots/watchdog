package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.interval.activityCheckers.CheckerTimerTask;

import org.eclipse.ui.IWorkbenchPart;

/** A base class for */
public abstract class ActiveUserActivityIntervalBase extends ActiveIntervalBase {

	/** The typing task. */
	protected CheckerTimerTask task;

	/** Constructor. */
	public ActiveUserActivityIntervalBase(IWorkbenchPart part) {
		super(part);
	}

	@Override
	public void listenForReactivation() {
		// TODO (MMB) task would be null if addTimeoutListener had not been
		// called before listenForReactivation ?
		assert (task != null);
		task.createListenerForReactivation();
	}
}