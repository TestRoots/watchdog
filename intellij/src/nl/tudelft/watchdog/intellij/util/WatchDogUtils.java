package nl.tudelft.watchdog.intellij.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogUtilsBase;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WatchDogUtils extends WatchDogUtilsBase {

    private static Project activeProject;

    private static Set<String> isWatchDogActive = new HashSet<>();

    /**
     * Returns the contents of the editor.
     */
    public static String getEditorContent(final Editor editor)
            throws IllegalArgumentException {
        if (editor == null) {
            throw new IllegalArgumentException("Editor is null");
        }
        return editor.getDocument().getText();
    }

    /**
     * Reads and returns the contents of a file from the supplied editor, by
     * trying to access the file from the disk.
     */
    public static String getContentForEditorFromDisk(VirtualFile virtualFile) {
        final String contents;
        if(virtualFile.getLength() > WatchDogUtilsBase.MAX_FILE_SIZE) {
        	return "";
        }

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
        return getProject().getName();
    }


    /** Returns current Project. */
    public static Project getProject() {
        return activeProject;
    }

    /**
     * Returns the {@link ProjectPreferenceSetting} of the currently active
     * project.
     */
    public static ProjectPreferenceSetting getProjectSetting() {
        return Preferences.getInstance().getOrCreateProjectSetting(getProjectName());
    }

    /** Set if WatchDog is active for current project. */
    public static void setWatchDogActiveForProject (Project project) {
        isWatchDogActive.add(project.getName());
    }

    /** Whether or not WatchDog is active for current project. */
    public static boolean isWatchDogActive(Project project) {
        return isWatchDogActive.contains(project.getName());
    }

    /** Sets currently active project (i.e. project that has focus) */
    public static void setActiveProject(Project activeProject) {
        WatchDogUtils.activeProject = activeProject;
        WatchDogGlobals.isActive = isWatchDogActive(activeProject);
    }
}
