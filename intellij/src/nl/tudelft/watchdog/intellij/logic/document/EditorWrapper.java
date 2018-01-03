package nl.tudelft.watchdog.intellij.logic.document;

import com.intellij.openapi.editor.Editor;
import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;

/**
 * Wrapper class for IntelliJ editor.
 */
public class EditorWrapper implements EditorWrapperBase {
    /** The {@link com.intellij.openapi.editor.Editor} associated with this interval. */
    protected transient Editor editor;

    public EditorWrapper(Editor editor) {
        this.editor = editor;
    }

    public Editor getEditor() {
        return this.editor;
    }
}
