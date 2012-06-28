package nl.tudelft.watchdog.timeDistributionPlugin.commands;

import java.io.File;


import nl.tudelft.watchdog.interval.IInterval;
import nl.tudelft.watchdog.interval.IIntervalKeeper;
import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MessageConsoleManager;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.console.MessageConsoleStream;


public class HelloHandler extends AbstractHandler{

	MessageConsoleStream stream = MessageConsoleManager.getConsoleStream();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		stream.println("Wroof!");
		
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		for(IInterval interval : intervalKeeper.getRecordedIntervals()){
			 stream.println(interval.getDocument().getFileName() +"\t\t" + interval.getDurationString()+ "\t\t" + interval.getStart()+" - "+interval.getEnd());			 
		}
		
		new IntervalsToXMLWriter().intervalsToXML(IntervalKeeper.getInstance().getRecordedIntervals(), new File("intervals.xml"));
		
		return null;
	}
}   