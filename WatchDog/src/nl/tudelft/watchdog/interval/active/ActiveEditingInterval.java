package nl.tudelft.watchdog.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.activityCheckers.EditingCheckerTask;
import nl.tudelft.watchdog.interval.activityCheckers.OnInactiveCallBack;

import org.eclipse.ui.IWorkbenchPart;

public class ActiveEditingInterval extends ActiveInterval {

	private EditingCheckerTask task;

	/**
	 * @param editor
	 *            the editor in this interval
	 */
	public ActiveEditingInterval(IWorkbenchPart part) {
		super(part);

		checkForChangeTimer = new Timer();
	}

	@Override
	public void addTimeoutListener(long timeout,
			OnInactiveCallBack callbackWhenFinished) {
		task = new EditingCheckerTask(this.getPart(), callbackWhenFinished);
		checkForChangeTimer.schedule(new EditingCheckerTask(this.getEditor(),
				callbackWhenFinished), timeout, timeout);
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.Editing;
	}

	@Override
	public void listenForReactivation() {
		assert (task != null);
		task.listenForReactivation();
	}

}
