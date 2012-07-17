package nl.tudelft.watchdog.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.activityCheckers.ChangerCheckerTask;
import nl.tudelft.watchdog.interval.activityCheckers.RunCallBack;

import org.eclipse.ui.texteditor.ITextEditor;

public class ActiveEditingInterval extends ActiveInterval {
	private Timer checkForChangeTimer;
	
	/**
	 * @param editor
	 * 		the editor in this interval
	 */
	public ActiveEditingInterval(ITextEditor editor){
		super(editor);
		
		checkForChangeTimer = new Timer();
	}
	
	@Override
	public void addTimeoutListener(long timeout, RunCallBack callbackWhenFinished){
		checkForChangeTimer.schedule(new ChangerCheckerTask(this.getEditor(), callbackWhenFinished), timeout, timeout);
	}
	
	public Timer getTimer(){
		return checkForChangeTimer;
	}

	@Override
	public void closeInterval() {
		this.isClosed = true;
		checkForChangeTimer.cancel();
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.Editing;
	}
	
}
