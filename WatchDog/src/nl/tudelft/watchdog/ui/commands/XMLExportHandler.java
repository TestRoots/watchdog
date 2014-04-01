package nl.tudelft.watchdog.ui.commands;

import java.util.List;

import nl.tudelft.watchdog.logic.exceptions.FileSavingFailedException;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
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
		WatchDogLogger.logInfo("exporting all intervals...");

		IntervalManager.getInstance().closeAllCurrentIntervals();

		List<IntervalBase> completeList = WatchDogUtils
				.getAllRecordedIntervals();

		try {
			UserPrompter.saveIntervalsToFile(completeList);
			WatchDogLogger.logInfo("exporting done.");
		} catch (FileSavingFailedException e) {
			WatchDogLogger.logSevere(e);
			UserPrompter.showMessageBox("Watchdog",
					"File could not be saved, please try again.");
		}
		return null;
	}
}