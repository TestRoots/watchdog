package timeDistributionPlugin;

import interval.IIntervalKeeper;
import interval.IntervalKeeper;
import interval.events.ClosingIntervalEvent;
import interval.events.IIntervalListener;
import interval.events.NewIntervalEvent;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.eclipse.ui.IStartup;

import timeDistributionPlugin.logging.MessageConsoleManager;
import timeDistributionPlugin.logging.MyLogger;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {
		setUpLogger();
		
		MyLogger.logSevere("Plugin startup");
		
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		intervalKeeper.addIntervalListener(new IIntervalListener() {
			
			@Override
			public void onNewInterval(NewIntervalEvent evt) {				
				MyLogger.logInfo("New interval: "+ evt.getInterval().getEditor().getTitle());				
			}
			
			@Override
			public void onClosingInterval(ClosingIntervalEvent evt) {
				MyLogger.logInfo("Closing interval "+ evt.getInterval().getDocument().getFileName() + " \n " + evt.getInterval().getStart() + " - " + evt.getInterval().getEnd() );
			}
		});		
	}

	private void setUpLogger(){
		SimpleFormatter fmt = new SimpleFormatter();
		StreamHandler sh = new StreamHandler(MessageConsoleManager.getConsoleStream(), fmt);		
		MyLogger.addHandler(sh, Level.SEVERE);
		
		try {
			MyLogger.addHandler(new FileHandler("watchdog.log", true), Level.ALL);
		} catch (SecurityException e) {
			MyLogger.logSevere(e.getMessage());
		} catch (IOException e) {
			MyLogger.logSevere(e.getMessage());
		}
		
		
	}
}
