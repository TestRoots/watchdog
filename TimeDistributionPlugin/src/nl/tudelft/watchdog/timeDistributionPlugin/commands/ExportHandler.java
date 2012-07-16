package nl.tudelft.watchdog.timeDistributionPlugin.commands;

import nl.tudelft.watchdog.exceptions.FileSavingFailedException;
import nl.tudelft.watchdog.interval.IIntervalKeeper;
import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MessageConsoleManager;
import nl.tudelft.watchdog.timeDistributionPlugin.prompts.UserPrompter;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.console.MessageConsoleStream;


public class ExportHandler implements IWorkbenchWindowActionDelegate{

	private MessageConsoleStream stream;
	
	public ExportHandler() {
		stream = MessageConsoleManager.getConsoleStream();
	}	

	@Override
	public void run(IAction action) {
		stream.println("Wroof!");		
		
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		for(IInterval interval : intervalKeeper.getRecordedIntervals()){
			 stream.println(interval.getDocument().getFileName() +"\t\t" + interval.getDurationString()+ "\t\t" + interval.getStart()+" - "+interval.getEnd());			 
		}
		
		try {
			UserPrompter.saveIntervalsToFile(new IntervalsToXMLWriter(), IntervalKeeper.getInstance().getRecordedIntervals());
		} catch (FileSavingFailedException e) {
			UserPrompter.showMessageBox("Watchdog", "File could not be saved, please try again.");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {}

	@Override
	public void dispose() {}

	@Override
	public void init(IWorkbenchWindow window) {}
	
}   