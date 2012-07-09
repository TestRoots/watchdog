package nl.tudelft.watchdog.timeDistributionPlugin.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


import nl.tudelft.watchdog.interval.IInterval;
import nl.tudelft.watchdog.interval.IIntervalKeeper;
import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MessageConsoleManager;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;
import nl.tudelft.watchdog.timingOutput.IIntervalWriter;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.console.MessageConsoleStream;


public class ExportHandler extends AbstractHandler{

	private MessageConsoleStream stream;
	private IIntervalWriter intervalWriter;
	
	public ExportHandler() {
		stream = MessageConsoleManager.getConsoleStream();
		intervalWriter = new IntervalsToXMLWriter();
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		stream.println("Wroof!");
		
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		for(IInterval interval : intervalKeeper.getRecordedIntervals()){
			 stream.println(interval.getDocument().getFileName() +"\t\t" + interval.getDurationString()+ "\t\t" + interval.getStart()+" - "+interval.getEnd());			 
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(new File("intervals.xml"));
			intervalWriter.exportIntervals(IntervalKeeper.getInstance().getRecordedIntervals(), fos);
		} catch (FileNotFoundException e) {
			MyLogger.logSevere(e);
		}
		return null;
	}
}   