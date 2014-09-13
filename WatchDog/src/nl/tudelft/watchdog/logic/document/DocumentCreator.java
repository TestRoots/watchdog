package nl.tudelft.watchdog.logic.document;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
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
		long beginDate = System.nanoTime();
		String activeProjectName;
		if (editor.getEditorInput() instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
			IProject activeProject = input.getFile().getProject();
			activeProjectName = activeProject.getName();
		} else {
			activeProjectName = "";
		}
		String title = "";
		if (!StringUtilities.isEmpty(editor.getTitle())) {
			title = editor.getTitle();
		}
		long endDate = System.nanoTime();
		WatchDogLogger.getInstance().logInfo(
				"get1: " + Long.toString(endDate - beginDate));

		try {
			return new Document(activeProjectName, title,
					getFileContent(editor));
		} catch (IllegalArgumentException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}
		return new Document(activeProjectName, title, "");
	}

	private static String getFileContent(ITextEditor editor) {
		long beginDate = System.nanoTime();
		String editorContent = "";
		try {
			long lbeginDate = System.nanoTime();

			editorContent = WatchDogUtils.getEditorContent(editor);
			long lendDate = System.nanoTime();
			WatchDogLogger.getInstance().logInfo(
					"inner getFileContent: "
							+ Long.toString(lendDate - lbeginDate));
		} catch (IllegalArgumentException | ContentReaderException exception) {
			// Editor was null, there is nothing we can do to get the file
			// contents.
			WatchDogLogger.getInstance().logSevere(exception);
			WatchDogLogger
					.getInstance()
					.logInfo(
							"Document (provider) was null, trying to read resource file contents.");
			try {
				editorContent = WatchDogUtils
						.getContentForEditorFromDisk(editor);
			} catch (IllegalArgumentException ex) {
				WatchDogLogger.getInstance().logInfo(
						"File does not exist anymore: " + editor.getTitle());
			}
		}
		long endDate = System.nanoTime();
		WatchDogLogger.getInstance().logInfo(
				"getFileContent: " + Long.toString(endDate - beginDate));
		return editorContent;
	}
}
