package nl.tudelft.watchdog.plugin.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.exceptions.FileSavingFailedException;
import nl.tudelft.watchdog.interval.IIntervalKeeper;
import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.interval.recorded.IRecordedIntervalSerializationManager;
import nl.tudelft.watchdog.interval.recorded.RecordedIntervalSerializationManager;
import nl.tudelft.watchdog.plugin.logging.MessageConsoleManager;
import nl.tudelft.watchdog.plugin.logging.MyLogger;
import nl.tudelft.watchdog.plugin.prompts.UserPrompter;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.console.MessageConsoleStream;

public class ExportHandler implements IWorkbenchWindowActionDelegate{

	private MessageConsoleStream stream;
	private IRecordedIntervalSerializationManager serializationManager;
	
	
	public ExportHandler() {
		stream = MessageConsoleManager.getConsoleStream();
		serializationManager = new RecordedIntervalSerializationManager();
	}	

	@Override
	public void run(IAction action) {
		stream.println("Wroof!");		
		
		IntervalKeeper.getInstance().closeAllCurrentIntervals();
		
		List<IInterval> completeList = getAllRecordedIntervals();
		for(IInterval interval : completeList){
			 stream.println(interval.getDocument().getFileName() +"\t\t" + interval.getDurationString()+ "\t\t" + interval.getStart()+" - "+interval.getEnd());			 
		}
		
		try {
			UserPrompter.saveIntervalsToFile(new IntervalsToXMLWriter(), completeList);
		} catch (FileSavingFailedException e) {
			MyLogger.logSevere(e);
			UserPrompter.showMessageBox("Watchdog", "File could not be saved, please try again.");
		}
	}

	private List<IInterval> getAllRecordedIntervals() {
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		List<IInterval> completeList = new ArrayList<IInterval>();
		try {
			completeList.addAll(serializationManager.retrieveRecordedIntervals());
		} catch (IOException e1) {
			MyLogger.logSevere(e1);
		} catch (ClassNotFoundException e1) {
			MyLogger.logSevere(e1);
		}
		completeList.addAll(intervalKeeper.getRecordedIntervals());
		return completeList;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {}

	@Override
	public void dispose() {}

	@Override
	public void init(IWorkbenchWindow window) {}
	
}   