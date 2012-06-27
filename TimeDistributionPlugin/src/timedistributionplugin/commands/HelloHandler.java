package timeDistributionPlugin.commands;

import interval.IInterval;
import interval.IIntervalKeeper;
import interval.IntervalKeeper;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.console.MessageConsoleStream;

import timeDistributionPlugin.logging.MessageConsoleManager;

public class HelloHandler extends AbstractHandler{

	MessageConsoleStream stream = MessageConsoleManager.getConsoleStream();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		stream.println("Wroof!");
		
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		for(IInterval interval : intervalKeeper.getRecordedIntervals()){
			 stream.println(interval.getDocument().getFileName() +"\t\t" + interval.getDurationString()+ "\t\t" + interval.getStart()+" - "+interval.getEnd());
			 
		}
		
		return null;
	}
}   