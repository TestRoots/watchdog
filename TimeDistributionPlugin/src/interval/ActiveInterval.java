package interval;

import interval.activityCheckers.ChangerCheckerTask;
import interval.activityCheckers.RunCallBack;

import java.util.Date;
import java.util.Timer;

import org.eclipse.ui.texteditor.ITextEditor;

public class ActiveInterval {
	private Timer checkForChangeTimer;
	private Date timeOfCreation;
	
	protected ITextEditor editor;
	
	
	/**
	 * 
	 * @param editor
	 * 		the editor in this interval
	 * @param timeout
	 * 		in millisecond
	 */
	public ActiveInterval(ITextEditor editor){
		this.editor = editor;
		this.timeOfCreation = new Date();
		checkForChangeTimer = new Timer();
	}
	
	public void start(long timeout, RunCallBack callbackWhenFinished){
		checkForChangeTimer.schedule(new ChangerCheckerTask(editor, callbackWhenFinished), timeout, timeout);
	}
	
	public Timer getTimer(){
		return checkForChangeTimer;
	}	
	
	public ITextEditor getEditor(){
		return editor;
	}
	
	public Date getTimeOfCreation(){
		return timeOfCreation;
	}
}
