package nl.tudelft.watchdog.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class WatchDogUtils {

    /**
     * @return A hash code for the given String, so that it is completely
     *         anonymous.
     */
    public static String createHash(String name) {
        return DigestUtils.shaHex(name);
    }

    /**
     * Generates an intelligent hash code of the supplied fileName, removing
     * .java file endings and shortening fully-qualified filenames.
     *
     * <br>
     * Example 1: Generates a hash code <code>a</code> for input "AClass.java"
     * and <code>aTest</code> for "AClassTest.java". <br>
     * <br>
     * Example 2: Takes <code>package.for.a</code> and returns the same file
     * hash as <code>a</code>.
     *
     * @return A hash for the given filename.
     */
    public static String createFileNameHash(String fileName) {
        String hashedName = "";
        if (isEmpty(fileName)) {
            return hashedName;
        }
        String lowerCaseFileName = fileName.toLowerCase().replaceFirst(
                Pattern.quote(".") + "java$", "");

        // Strip-away fully-qualified path from filename (necessary when project
        // has Maven nature)
        String[] fileNameParts = lowerCaseFileName.split(Pattern.quote("."));
        lowerCaseFileName = fileNameParts[fileNameParts.length - 1];

        if (lowerCaseFileName.startsWith("test")
                || lowerCaseFileName.endsWith("test")) {
            lowerCaseFileName = lowerCaseFileName.replaceFirst("^test", "");
            lowerCaseFileName = lowerCaseFileName.replaceFirst("test$", "");
            hashedName = createHash(lowerCaseFileName) + "Test";
        } else {
            hashedName = createHash(lowerCaseFileName);
        }
        return hashedName;
    }

    /**
     * @return the number of source lines of code in the given string.
     */
    public static long countSLOC(String text) {
        String[] lines = text.split("\r\n|\r|\n");
        long sloc = 0;
        for (String line : lines) {
            if (!isEmptyOrHasOnlyWhitespaces(line)) {
                sloc++;
            }
        }
        return sloc;
    }

    /**
     * @return Whether the string with white spaces trimmed is empty.
     */
    public static boolean isEmptyOrHasOnlyWhitespaces(String string) {
        return isEmpty(string) ? true : string.trim().isEmpty();
    }

    /**
     * @return <code>true</code> when the given string is either
     *         <code>null</code> or empty. <code>false</code> otherwise.
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

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
}
