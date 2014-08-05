package nl.tudelft.watchdog.logic.interval.activityCheckers;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.logic.interval.intervaltypes.UserActivityIntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A task for checking whether the user is typing. */
public class TypingCheckerTask extends CheckerTimerTask {

	/** The editor. */
	private ITextEditor editor;

	/** Constructor. */
	public TypingCheckerTask(IWorkbenchPart part,
			UserActivityIntervalBase interval) {
		super(interval);
		this.editor = (ITextEditor) part;
		this.stillActiveChecker = new EditorContentChangedChecker(editor);
	}

	@Override
	public void run() {
		try {
			if (stillActiveChecker.hasChanged()) {
				return;
			} else {
				WatchDogLogger.getInstance().logInfo("Not changed!");
				// not an active document any longer, as no changes had been
				// made.
				// (1) stop the timer
				cancel();
			}
		} catch (EditorClosedPrematurelyException e) {
			// this can happen when eclipse is closed while the document is
			// still active
			WatchDogLogger.getInstance().logInfo("Editor closed prematurely");
			cancel();
		} catch (ContentReaderException e) {
			// this can happen when a file is moved inside the workspace
			WatchDogLogger.getInstance().logInfo("Unavailable doc provider");
			cancel();
		}

		interval.closeInterval();
	}

	// IDocumentProvider documentProvider = editor.getDocumentProvider();
	// final IDocument document = documentProvider.getDocument(editor
	// .getEditorInput());
	//
	// final IDocumentListener docListener = new IDocumentListener() {
	//
	// @Override
	// public void documentChanged(DocumentEvent event) {
	// // listen to this event just once, notify that the document is
	// // activated, then remove this listener
	// IntervalManager
	// .getInstance()
	// .getEditorObserveable()
	// .notifyObservers(
	// new FocusStartEditorEvent(workbenchPart));
	// document.removeDocumentListener(this);
	// }
	//
	// @Override
	// public void documentAboutToBeChanged(DocumentEvent event) {
	// }
	// };
	//
	// document.addDocumentListener(docListener);
}
