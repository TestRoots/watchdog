package nl.tudelft.watchdog.interval.active;

import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.activityCheckers.RunCallBack;

import org.eclipse.ui.texteditor.ITextEditor;

public class ActiveReadingInterval extends ActiveInterval {
	
	
	/**
	 * @param editor
	 * 		the editor in this interval
	 */
	public ActiveReadingInterval(ITextEditor editor){
		super(editor);
	}

	@Override
	public void addTimeoutListener(long timeout,
			RunCallBack callbackWhenFinished) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.Reading;
	}
}
