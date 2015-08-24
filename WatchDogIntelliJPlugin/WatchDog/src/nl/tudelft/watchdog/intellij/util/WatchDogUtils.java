package nl.tudelft.watchdog.intellij.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import nl.tudelft.watchdog.intellij.WatchDog;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.ContentReaderException;
import nl.tudelft.watchdog.core.util.WatchDogUtilsBase;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WatchDogUtils extends WatchDogUtilsBase {

    /**
     * Returns the contents of the editor.
     */
    public static String getEditorContent(final Editor editor)
            throws ContentReaderException, IllegalArgumentException {
        if (editor == null) {
            throw new IllegalArgumentException("Editor is null");
        }
        Document document = editor.getDocument();
        if (document == null) {
            throw new ContentReaderException("Document is null");
        }
        return document.getText();
    }

    /**
     * Reads and returns the contents of a file from the supplied editor, by
     * trying to access the file from the disk.
     */
    public static String getContentForEditorFromDisk(VirtualFile virtualFile) {
        final String contents;
        try {
            BufferedReader br = new BufferedReader(new FileReader(virtualFile.getPath()));
            String currentLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = br.readLine()) != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
            }
            contents = stringBuilder.toString();
        } catch (IOException e1) {
            return null;
        }
        return contents;
    }


    /**
     * Returns the Project's name.
     */
    public static String getProjectName() {
        return WatchDog.project.getName();
    }

    /**
     * Returns the {@link ProjectPreferenceSetting} of the currently active
     * project.
     */
    public static ProjectPreferenceSetting getProjectSetting() {
        return Preferences.getInstance().getOrCreateProjectSetting(getProjectName());
    }
}
