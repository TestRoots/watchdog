package nl.tudelft.watchdog.logic.interval.activityCheckers;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A task for checking whether the user is typing. */
public class TypingCheckerTask extends CheckerTimerTask {

	/** The editor. */
	private ITextEditor editor;

	/** The workbenchPart this editor belongs to. */
	private IWorkbenchPart workbenchPart;

	/** Constructor. */
	public TypingCheckerTask(IWorkbenchPart part) {
		this.editor = (ITextEditor) part;
		this.workbenchPart = part;
		this.stillActiveChecker = new EditorContentChangedChecker(editor);
	}

	@Override
	public void run() {
		try {
			if (!stillActiveChecker.hasChanged()) {
				WatchDogLogger.getInstance().logInfo("Checker has changed!");
				// not an active document any longer, as no changes had been
				// made.
				// (1) stop the timer
				cancel();
				// (2) listen to changes in open, inactive documents
			}
		} catch (EditorClosedPrematurelyException e) {
			// this can happen when eclipse is closed while the document is
			// still active
			WatchDogLogger.getInstance().logInfo("Editor closed prematurely");
		} catch (ContentReaderException e) {
			// this can happen when a file is moved inside the workspace
			WatchDogLogger.getInstance().logInfo("Unavailable doc provider");
		}
	}
}
