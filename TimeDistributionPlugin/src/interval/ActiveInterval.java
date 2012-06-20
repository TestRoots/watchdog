package interval;

import java.util.Timer;

import org.eclipse.ui.texteditor.ITextEditor;

public class ActiveInterval {
	private Timer checkForChangeTimer;
	
	/**
	 * 
	 * @param editor
	 * 		the editor in this interval
	 * @param timeout
	 * 		in millisecond
	 */
	public ActiveInterval(ITextEditor editor, long timeout){
		
		checkForChangeTimer = new Timer();		
		
		checkForChangeTimer.schedule(new ChangerCheckerTask(editor), timeout, timeout);
	}
}
