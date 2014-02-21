package nl.tudelft.watchdog.document;

import nl.tudelft.watchdog.exceptions.ContentReaderException;
import nl.tudelft.watchdog.plugin.logging.WDLogger;
import nl.tudelft.watchdog.util.WatchDogUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class DocumentFactory implements IDocumentFactory {
	@Override
	public Document createDocument(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;

			if (part instanceof IEditorPart) {

				IEditorPart editorPart = (IEditorPart) part;
				String activeProjectName;
				if (editorPart.getEditorInput() instanceof IFileEditorInput) {
					IFileEditorInput input = (IFileEditorInput) editorPart
							.getEditorInput();
					IFile file = input.getFile();
					IProject activeProject = file.getProject();
					activeProjectName = activeProject.getName();
				} else {
					activeProjectName = "";
				}

				DocumentType docType = DocumentType.UNDEFINED;
				try {
					docType = DocumentClassifier.classifyDocument(
							editorPart.getTitle(),
							WatchDogUtil.getEditorContent(editor));
				} catch (IllegalArgumentException e) {
					WDLogger.logSevere(e);
				} catch (ContentReaderException e) {
					WDLogger.logInfo("Document provider was null, trying to read resource file contents");
					try {
						docType = DocumentClassifier.classifyDocument(
								editorPart.getTitle(),
								WatchDogUtil.getFileContentsFromEditor(editor));
					} catch (IllegalArgumentException ex) {
						WDLogger.logInfo("File does not exist anymore: "
								+ editor.getTitle());
					}
				}

				return new Document(activeProjectName, editor.getTitle(),
						docType);
			} else {
				throw new IllegalArgumentException("Part not an IEditorPart");
			}
		} else {
			throw new IllegalArgumentException("Part not an ITextEditor");
		}
	}
}
