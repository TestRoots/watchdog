package nl.tudelft.watchdog.logic.document;

import com.intellij.openapi.editor.Editor;
import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;

/**
 * Wrapper class for IntelliJ editor.
 */
public class EditorWrapper extends EditorWrapperBase {
    /** The {@link com.intellij.openapi.editor.Editor} associated with this interval. */
    protected transient Editor editor;

    public EditorWrapper(Editor editor) {
        this.editor = editor;
    }

    public Editor getEditor() {
        return this.editor;
    }
}
