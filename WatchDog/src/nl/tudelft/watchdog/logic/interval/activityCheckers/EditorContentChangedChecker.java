package nl.tudelft.watchdog.logic.interval.activityCheckers;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.logic.logging.WDLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.ui.texteditor.ITextEditor;

/**
 * An {@link IUpdateChecker} that compares the previous editor content to the
 * current content to see if it changed.
 */
public class EditorContentChangedChecker implements IUpdateChecker {

	/** The {@link ITextEditor} we are receiving the file contents from. */
	private ITextEditor editor;

	/** Previous Editor content. */
	private String previousContent;

	/** Current Editor content. */
	private String currentContent;

	/** Constructor. */
	public EditorContentChangedChecker(ITextEditor editor) {
		this.editor = editor;
		try {
			this.previousContent = WatchDogUtils.getEditorContent(editor);
		} catch (IllegalArgumentException e) {
			this.previousContent = "";
			WDLogger.logSevere(e);
		} catch (ContentReaderException e) {
			this.previousContent = "";
			WDLogger.logSevere(e);
		}
	}

	@Override
	public boolean hasChanged() throws EditorClosedPrematurelyException,
			ContentReaderException {
		try {
			currentContent = WatchDogUtils.getEditorContent(editor);
		} catch (IllegalArgumentException ex) {
			throw new EditorClosedPrematurelyException();
		}
		boolean contentChanged = !previousContent.equals(currentContent);
		previousContent = currentContent;
		return contentChanged;
	}

}
