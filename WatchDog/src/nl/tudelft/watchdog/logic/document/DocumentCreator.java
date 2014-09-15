package nl.tudelft.watchdog.logic.document;

import nl.tudelft.watchdog.util.ContentReaderException;
import nl.tudelft.watchdog.util.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cedarsoftware.util.StringUtilities;

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
		if (editor.getEditorInput() instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
			IProject activeProject = input.getFile().getProject();
			activeProjectName = activeProject.getName();
		}
		String title = null;
		if (!StringUtilities.isEmpty(editor.getTitle())) {
			title = editor.getTitle();
		}

		try {
			return new Document(activeProjectName, title,
					getEditorOrFileContent(editor));
		} catch (IllegalArgumentException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}
		return new Document(activeProjectName, title, null);
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
			WatchDogLogger.getInstance().logSevere(exception);
			WatchDogLogger
					.getInstance()
					.logInfo(
							"Document (provider) was null, trying to read resource file contents.");
			try {
				return WatchDogUtils.getContentForEditorFromDisk(editor);
			} catch (IllegalArgumentException ex) {
				WatchDogLogger.getInstance().logInfo(
						"File does not exist anymore: " + editor.getTitle());
			}
		}
		return null;
	}
}
