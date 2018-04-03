package nl.tudelft.watchdog.eclipse.logic.document;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.util.ContentReaderException;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

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
        try {
            return createDocument(editor.getTitle(), WatchDogUtils.getFile(editor));
        } catch (IllegalArgumentException ignored) {
            try {
                return new Document("", editor.getTitle(), "", WatchDogUtils.getEditorContent(editor));
            } catch (IllegalArgumentException | ContentReaderException exception) {
                return new Document("", editor.getTitle(), "", "");
            }
        }

    }

    public static Document createDocument(String title, IFile file) {
        return createDocument(title, file, WatchDogUtils.getContentForFileFromDisk(file));
    }

    private static Document createDocument(String title, IFile file, String content) {
        String activeProjectName = null;
        String filePath = "";
        try {
            IProject activeProject = file.getProject();
            activeProjectName = activeProject.getName();
            filePath = file.getProjectRelativePath().toString();
        } catch (IllegalArgumentException ex) {
            // Intentionally left empty
        }

        try {
            return new Document(activeProjectName, title, filePath,
                    WatchDogUtils.getContentForFileFromDisk(file));
        } catch (IllegalArgumentException exception) {
            WatchDogLogger.getInstance().logSevere(exception);
        }
        return new Document(activeProjectName, title, filePath, null);
    }
}
