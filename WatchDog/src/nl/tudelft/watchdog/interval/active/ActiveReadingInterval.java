package nl.tudelft.watchdog.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.activityCheckers.ReadingCheckerTask;
import nl.tudelft.watchdog.interval.activityCheckers.OnInactiveCallBack;

import org.eclipse.ui.IWorkbenchPart;

public class ActiveReadingInterval extends ActiveInterval {
	
	private ReadingCheckerTask task;
	
	/**
	 * @param editor
	 * 		the editor in this interval
	 */
	public ActiveReadingInterval(IWorkbenchPart part){
		super(part);
		checkForChangeTimer = new Timer();
	}

	@Override
	public void addTimeoutListener(long timeout, final OnInactiveCallBack callbackWhenFinished) {
		task = new ReadingCheckerTask(this.getEditor(), callbackWhenFinished);
		checkForChangeTimer.schedule(new ReadingCheckerTask(this.getEditor(), callbackWhenFinished), 0, timeout);
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.Reading;
	}

	@Override
	public void listenForReactivation() {
		assert(task != null);
		task.listenForReactivation();
	}
}
