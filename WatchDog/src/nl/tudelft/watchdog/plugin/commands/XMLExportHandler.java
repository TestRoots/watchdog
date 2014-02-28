package nl.tudelft.watchdog.plugin.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.exceptions.FileSavingFailedException;
import nl.tudelft.watchdog.interval.IIntervalManager;
import nl.tudelft.watchdog.interval.IntervalManager;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.interval.recorded.IRecordedIntervalSerializationManager;
import nl.tudelft.watchdog.interval.recorded.RecordedIntervalSerializationManager;
import nl.tudelft.watchdog.plugin.logging.WDLogger;
import nl.tudelft.watchdog.plugin.prompts.UserPrompter;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * An export handler for exporting to XML
 */
public class XMLExportHandler extends AbstractHandler {

	private IRecordedIntervalSerializationManager serializationManager;

	/** Constructor. */
	public XMLExportHandler() {
		serializationManager = new RecordedIntervalSerializationManager();
	}

	private List<IInterval> getAllRecordedIntervals() {
		IIntervalManager intervalKeeper = IntervalManager.getInstance();
		List<IInterval> completeList = new ArrayList<IInterval>();
		try {
			completeList.addAll(serializationManager
					.retrieveRecordedIntervals());
		} catch (IOException exception) {
			WDLogger.logSevere(exception);
		} catch (ClassNotFoundException exception) {
			WDLogger.logSevere(exception);
		}
		completeList.addAll(intervalKeeper.getRecordedIntervals());
		return completeList;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WDLogger.logInfo("exporting all intervals...");

		IntervalManager.getInstance().closeAllCurrentIntervals();

		List<IInterval> completeList = getAllRecordedIntervals();

		try {
			UserPrompter.saveIntervalsToFile(new IntervalsToXMLWriter(),
					completeList);
			WDLogger.logInfo("exporting done.");
		} catch (FileSavingFailedException e) {
			WDLogger.logSevere(e);
			UserPrompter.showMessageBox("Watchdog",
					"File could not be saved, please try again.");
		}
		return null;
	}
}