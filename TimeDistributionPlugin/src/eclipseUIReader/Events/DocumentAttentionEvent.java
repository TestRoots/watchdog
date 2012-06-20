package eclipseUIReader.Events;

import java.util.EventObject;

import org.eclipse.ui.texteditor.ITextEditor;

import util.TextEditorContentReader;

@SuppressWarnings("serial")
public class DocumentAttentionEvent extends EventObject {
	ITextEditor editor;
	public DocumentAttentionEvent(ITextEditor source) {
		super(source);
		editor = source;				
	}
	public ITextEditor getChangedEditor(){
		return editor;
	}
	
	public String getContents(){		
		return TextEditorContentReader.getEditorContent(editor);
	}
}
