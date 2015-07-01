package nl.tudelft.watchdog.logic.document;

import nl.tudelft.watchdog.core.util.ContentReaderException;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A factory for creating {@link Document}s from a supplied {@link ITextEditor}.
 */
public class DocumentCreator {
	/**
	 * Factory method that creates and returns a {@link Document} from a given
	 * {@link IWorkbenchPart}. For this to succeed, it is necessary that the the
	 * supplied part is an IEditorPart.
	 */
	public static Document createDocument(ITextEditor editor) {
		String activeProjectName = null;
		String filePath = "";
		String title = "";
		try {
			title = editor.getTitle();
			IFile file = WatchDogUtils.getFile(editor);
			IProject activeProject = file.getProject();
			activeProjectName = activeProject.getName();
			filePath = file.getProjectRelativePath().toString();
		} catch (IllegalArgumentException ex) {
			// Intentionally left empty
		}

		try {
			return new Document(activeProjectName, title, filePath,
					getEditorOrFileContent(editor));
		} catch (IllegalArgumentException exception) {
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logSevere(
					exception);
		}
		return new Document(activeProjectName, title, filePath, null);
	}

	/**
	 * Gets the contents of the given editor. If it cannot get those, tries to
	 * get the file from disk. If this fails, too, returns <code>null</code>.
	 */
	private static String getEditorOrFileContent(ITextEditor editor) {
		try {
			return WatchDogUtils.getEditorContent(editor);
		} catch (IllegalArgumentException | ContentReaderException exception) {
			// Editor was null, there is nothing we can do to get the file
			// contents.
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logSevere(
					exception);
			WatchDogLogger
					.getInstance(Preferences.getInstance().isLoggingEnabled())
					.logInfo(
							"Document (provider) was null, trying to read resource file contents.");
			try {
				return WatchDogUtils.getContentForEditorFromDisk(editor);
			} catch (IllegalArgumentException ex) {
				WatchDogLogger.getInstance(
						Preferences.getInstance().isLoggingEnabled()).logInfo(
						"File does not exist anymore: " + editor.getTitle());
			}
		}
		return null;
	}
}
