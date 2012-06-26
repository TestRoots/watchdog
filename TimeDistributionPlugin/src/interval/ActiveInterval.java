package interval;

import interval.activityCheckers.ChangerCheckerTask;
import interval.activityCheckers.ChangerCheckerTask.RunCallBack;

import java.util.Timer;

import org.eclipse.ui.texteditor.ITextEditor;

class ActiveInterval extends Interval {
	private Timer checkForChangeTimer;
	
	/**
	 * 
	 * @param editor
	 * 		the editor in this interval
	 * @param timeout
	 * 		in millisecond
	 */
	public ActiveInterval(ITextEditor editor){
		super(editor);
		checkForChangeTimer = new Timer();
	}
	
	public void start(long timeout, RunCallBack callbackWhenFinished){
		checkForChangeTimer.schedule(new ChangerCheckerTask(editor, callbackWhenFinished), timeout, timeout);
	}
	
	public Timer getTimer(){
		return checkForChangeTimer;
	}
}
