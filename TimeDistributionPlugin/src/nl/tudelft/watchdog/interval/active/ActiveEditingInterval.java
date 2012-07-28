package nl.tudelft.watchdog.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.activityCheckers.EditingCheckerTask;
import nl.tudelft.watchdog.interval.activityCheckers.RunCallBack;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class ActiveEditingInterval extends ActiveInterval {
	
	private EditingCheckerTask task;
	
	/**
	 * @param editor
	 * 		the editor in this interval
	 */
	public ActiveEditingInterval(IWorkbenchPart part){
		super(part);
		
		checkForChangeTimer = new Timer();
	}
	
	@Override
	public void addTimeoutListener(long timeout, RunCallBack callbackWhenFinished){
		task = new EditingCheckerTask(this.getPart(), callbackWhenFinished);
		checkForChangeTimer.schedule(new EditingCheckerTask(this.getEditor(), callbackWhenFinished), timeout, timeout);
	}
	
	
	/*
	@Override
	public void closeInterval() {
		this.isClosed = true;
		checkForChangeTimer.cancel();
		listenForReactivation();
	}
*/
	@Override
	public ActivityType getActivityType() {
		return ActivityType.Editing;
	}

	@Override
	public void listenForReactivation() {
		assert(task != null);
		task.listenForReactivation();
	}
	
}
