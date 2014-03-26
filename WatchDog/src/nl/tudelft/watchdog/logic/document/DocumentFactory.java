package nl.tudelft.watchdog.logic.document;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.logging.WDLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A DocumentFactory for creating {@link Document}s.
 */
public class DocumentFactory {
	/**
	 * Factory method that creates and returns a {@link Document} from a given
	 * {@link IWorkbenchPart}. For this to succeed, it is necessary that the the
	 * supplied part is an IEditorPart.
	 */
	// TODO (MMB) This might return a document which is not alive any more (and
	// thus has an unknown file type)
	public Document createDocument(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) part;

			if (part instanceof IEditorPart) {
				IEditorPart editorPart = (IEditorPart) part;
				String activeProjectName;
				if (editorPart.getEditorInput() instanceof IFileEditorInput) {
					IFileEditorInput input = (IFileEditorInput) editorPart
							.getEditorInput();
					IProject activeProject = input.getFile().getProject();
					activeProjectName = activeProject.getName();
				} else {
					activeProjectName = "";
				}

				DocumentType documentType = determineDocumentType(editor,
						editorPart);

				return new Document(activeProjectName, editor.getTitle(),
						documentType);
			} else {
				throw new IllegalArgumentException("Part not an IEditorPart");
			}
		} else {
			throw new IllegalArgumentException("Part not an ITextEditor");
		}
	}

	/**
	 * @return The document type for the given editor and editorPart. If an
	 *         error occurred, {value DocumentType#UNDEFINED} is returned.
	 */
	private DocumentType determineDocumentType(ITextEditor editor,
			IEditorPart editorPart) {
		String editorContent = "";
		try {
			editorContent = WatchDogUtils.getEditorContent(editor);
		} catch (IllegalArgumentException exception) {
			// Editor was null, there is nothing we can do to get the file
			// contents.
			// TODO (MMB) Try read-in via IFile?
			WDLogger.logSevere(exception);
		} catch (ContentReaderException exception) {
			WDLogger.logInfo("Document (provider) was null, trying to read resource file contents.");
			try {
				editorContent = WatchDogUtils
						.getContentForEditorFromDisk(editor);
			} catch (IllegalArgumentException ex) {
				WDLogger.logInfo("File does not exist anymore: "
						+ editor.getTitle());
				// TODO (MMB) hm, in that case, wouldn't it be better to stop
				// the recording interval?
			}
		}
		return DocumentClassifier.classifyDocument(editorPart.getTitle(),
				editorContent);
	}
}
