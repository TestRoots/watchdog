package nl.tudelft.watchdog.logic.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.logic.interval.activityCheckers.CheckerTimerTask;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;
import nl.tudelft.watchdog.logic.interval.activityCheckers.ReadingCheckerTask;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class ReadingInterval extends UserActivityIntervalBase {

	private CheckerTimerTask task;

	/**
	 * @param editor
	 *            the editor in this interval
	 */
	public ReadingInterval(IWorkbenchPart part) {
		super(part);
		checkForChangeTimer = new Timer();
	}

	@Override
	public void addTimeoutListener(long timeout,
			final OnInactiveCallBack callbackWhenFinished) {
		task = new ReadingCheckerTask(this.getEditor(), callbackWhenFinished);
		checkForChangeTimer.schedule(new ReadingCheckerTask(this.getEditor(),
				callbackWhenFinished), 0, timeout);
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.Reading;
	}

	@Override
	public void listenForReactivation() {
		assert (task != null);
		task.createListenerForReactivation();
	}

	public IWorkbenchPart getPart() {
		return part;
	}

	public ITextEditor getEditor() {
		return editor;
	}
}
