package nl.tudelft.watchdog.logic.document;

import nl.tudelft.watchdog.core.logic.document.EditorWrapperBase;

import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Wrapper class for Eclipse editor.
 */
public class EditorWrapper extends EditorWrapperBase {

	/** The {@link ITextEditor}. */
	protected transient ITextEditor editor;

	public EditorWrapper(ITextEditor editor) {
		this.editor = editor;
	}

	/** Getting the current ITextEditor. */
	public ITextEditor getEditor() {
		return this.editor;
	}
}
