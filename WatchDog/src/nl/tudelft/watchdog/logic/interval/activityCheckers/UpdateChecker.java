package nl.tudelft.watchdog.logic.interval.activityCheckers;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.logic.logging.WDLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.ui.texteditor.ITextEditor;

public class UpdateChecker implements IUpdateChecker {

	private ITextEditor editor;
	private String previousContent;
	private String lastCheckedContent;

	public UpdateChecker(ITextEditor editor) {
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
			lastCheckedContent = WatchDogUtils.getEditorContent(editor);
		} catch (IllegalArgumentException ex) {
			throw new EditorClosedPrematurelyException();
		}
		boolean isChanged = !previousContent.equals(lastCheckedContent);
		previousContent = lastCheckedContent;
		return isChanged;
	}

}
