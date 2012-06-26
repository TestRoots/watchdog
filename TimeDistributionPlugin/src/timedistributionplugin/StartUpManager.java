package timeDistributionPlugin;

import interval.IIntervalKeeper;
import interval.IntervalKeeper;
import interval.events.IIntervalListener;
import interval.events.IntervalEvent;

import org.eclipse.ui.IStartup;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {
		MyLogger.logInfo("Plugin startup");
		
		IIntervalKeeper intervalKeeper = new IntervalKeeper();
		intervalKeeper.addIntervalListener(new IIntervalListener() {
			
			@Override
			public void onNewInterval(IntervalEvent evt) {				
				MyLogger.logInfo("New interval: "+ evt.getInterval().getEditor().getTitle());
			}
			
			@Override
			public void onClosingInterval(IntervalEvent evt) {
				MyLogger.logInfo("Closing interval"+ evt.getInterval().getEditor().getTitle());
			}
		});
		
	}

}
