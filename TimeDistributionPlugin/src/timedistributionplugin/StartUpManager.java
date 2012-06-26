package timeDistributionPlugin;

import interval.IIntervalKeeper;
import interval.IntervalKeeper;
import interval.events.ClosingIntervalEvent;
import interval.events.IIntervalListener;
import interval.events.NewIntervalEvent;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {	
		
		MessageConsole console = new MessageConsole("My Console", null);
		console.activate();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ console });
		MessageConsoleStream stream = console.newMessageStream();
		stream.println("Hello, world!");
		
		MyLogger.logInfo("Plugin startup");
		
		IIntervalKeeper intervalKeeper = new IntervalKeeper();
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

}
