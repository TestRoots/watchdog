package nl.tudelft.watchdog.logic.document;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A factory for creating {@link Document}s from a supplied {@link ITextEditor}.
 */
// TODO (MMB) The naming of this class is seriously flawed. It does not work
// like a Factory pattern (where different kinds of objects are created)
public class DocumentFactory {
	/**
	 * Factory method that creates and returns a {@link Document} from a given
	 * {@link IWorkbenchPart}. For this to succeed, it is necessary that the the
	 * supplied part is an IEditorPart.
	 */
	public Document createDocument(ITextEditor editor) {
		long beginDate = System.nanoTime();
		String activeProjectName;
		if (editor.getEditorInput() instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
			IProject activeProject = input.getFile().getProject();
			activeProjectName = activeProject.getName();
		} else {
			activeProjectName = "";
		}
		long endDate = System.nanoTime();
		WatchDogLogger.getInstance().logInfo(
				"get1: " + Long.toString(endDate - beginDate));

		try {
			String editorContent = getEditorContent(editor, editor);
			DocumentType documentType = DocumentClassifier.classifyDocument(
					editor.getTitle(), editorContent);

			return new Document(activeProjectName, editor.getTitle(),
					documentType, editorContent);
		} catch (IllegalArgumentException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}
		return new Document(activeProjectName, editor.getTitle(),
				DocumentType.UNDEFINED, "");
	}

	private String getEditorContent(ITextEditor editor, IEditorPart editorPart) {
		String editorContent = "";
		try {
			editorContent = WatchDogUtils.getEditorContent(editor);
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
		return editorContent;
	}
}
