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
import nl.tudelft.watchdog.plugin.logging.WDLogger;
import nl.tudelft.watchdog.plugin.prompts.UserPrompter;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ExportHandler implements IWorkbenchWindowActionDelegate{

	private IRecordedIntervalSerializationManager serializationManager;
	
	
	public ExportHandler() {
		serializationManager = new RecordedIntervalSerializationManager();
	}	

	@Override
	public void run(IAction action) {
		WDLogger.logInfo("exporting all intervals...");
		
		IntervalKeeper.getInstance().closeAllCurrentIntervals();
		
		List<IInterval> completeList = getAllRecordedIntervals();		
		
		try {
			UserPrompter.saveIntervalsToFile(new IntervalsToXMLWriter(), completeList);
			WDLogger.logInfo("exporting done.");
		} catch (FileSavingFailedException e) {
			WDLogger.logSevere(e);
			UserPrompter.showMessageBox("Watchdog", "File could not be saved, please try again.");
		}
	}

	private List<IInterval> getAllRecordedIntervals() {
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		List<IInterval> completeList = new ArrayList<IInterval>();
		try {
			completeList.addAll(serializationManager.retrieveRecordedIntervals());
		} catch (IOException e1) {
			WDLogger.logSevere(e1);
		} catch (ClassNotFoundException e1) {
			WDLogger.logSevere(e1);
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