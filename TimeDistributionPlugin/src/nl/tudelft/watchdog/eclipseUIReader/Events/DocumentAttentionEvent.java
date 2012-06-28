package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventObject;

import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("serial")
public class DocumentAttentionEvent extends EventObject {
	ITextEditor editor;
	public DocumentAttentionEvent(ITextEditor editor) {
		super(editor);
		this.editor = editor;				
	}

	public ITextEditor getChangedEditor(){
		return editor;
	}
}
