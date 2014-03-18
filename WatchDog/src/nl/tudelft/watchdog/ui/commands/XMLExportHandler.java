package nl.tudelft.watchdog.ui.commands;

import java.util.List;

import nl.tudelft.watchdog.logic.exceptions.FileSavingFailedException;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalsToXMLWriter;
import nl.tudelft.watchdog.logic.interval.recorded.IInterval;
import nl.tudelft.watchdog.logic.logging.WDLogger;
import nl.tudelft.watchdog.ui.UserPrompter;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * An export handler for exporting to XML
 */
public class XMLExportHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WDLogger.logInfo("exporting all intervals...");

		IntervalManager.getInstance().closeAllCurrentIntervals();

		List<IInterval> completeList = WatchDogUtils.getAllRecordedIntervals();

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