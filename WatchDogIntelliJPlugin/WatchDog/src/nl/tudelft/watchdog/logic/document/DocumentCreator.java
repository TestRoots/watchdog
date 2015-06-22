package nl.tudelft.watchdog.logic.document;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import nl.tudelft.watchdog.util.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Editor;

/**
 * A factory for creating {@link nl.tudelft.watchdog.logic.document.Document}s from a supplied {@link Editor}.
 */
public class DocumentCreator {
    /**
     * Factory method that creates and returns a {@link nl.tudelft.watchdog.logic.document.Document} from a given
     * {@link Editor}. For this to succeed, it is necessary that the the
     * supplied part is Project.
     */
    public static Document createDocument(Editor editor)  {
        String activeProjectName = null;
        String filePath = "";
        String title = "";
        Project project = editor.getProject();
        try {
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

            activeProjectName = project.getName();
            filePath = virtualFile.getPath();
            title = virtualFile.getName();
        } catch (NullPointerException ex) {
            // Intentionally left empty
        }

        try {
            return new Document(activeProjectName, title, filePath,
                    getEditorOrFileContent(editor));
        } catch (IllegalArgumentException exception) {
            WatchDogLogger.getInstance().logSevere(exception);
        }
        return new Document(activeProjectName, title, filePath, null);
    }

    /**
     * Gets the contents of the given editor. If it cannot get those, tries to
     * get the file from disk. If this fails, too, returns <code>null</code>.
     */
    private static String getEditorOrFileContent(Editor editor) {
        try {
            return WatchDogUtils.getEditorContent(editor);
        } catch (Exception exception) {
            WatchDogLogger.getInstance().logSevere(exception);
            WatchDogLogger
                    .getInstance()
                    .logInfo(
                            "Document was null, trying to read resource file contents.");
            try {
                VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
                return WatchDogUtils.getContentForEditorFromDisk(virtualFile);
            } catch (IllegalArgumentException ex) {
                WatchDogLogger.getInstance().logInfo(
                        "File does not exist anymore." );
            }
        }
        return null;
    }
}
