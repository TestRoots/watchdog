package nl.tudelft.watchdog.plugin.commands;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import nl.tudelft.watchdog.interval.IIntervalKeeper;
import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.interval.events.ClosingIntervalEvent;
import nl.tudelft.watchdog.interval.events.IIntervalListener;
import nl.tudelft.watchdog.interval.events.NewIntervalEvent;
import nl.tudelft.watchdog.plugin.logging.MessageConsoleManager;
import nl.tudelft.watchdog.plugin.logging.MyLogger;

import org.eclipse.ui.IStartup;


public class StartUpHandler implements IStartup {

	@Override
	public void earlyStartup() {
		setUpLogger();
		
		MyLogger.logInfo("Plugin recording..."); 
		
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
			FileHandler fileHandler = new FileHandler("watchdoglog.log", true);
			fileHandler.setFormatter(fmt);
			MyLogger.addHandler(fileHandler, Level.ALL);
		} catch (SecurityException e) {
			MyLogger.logSevere(e.getMessage());
		} catch (IOException e) {
			MyLogger.logSevere(e.getMessage());
		}		
	}
}