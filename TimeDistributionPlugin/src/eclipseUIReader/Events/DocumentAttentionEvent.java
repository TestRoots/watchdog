package eclipseUIReader.Events;

import java.util.EventObject;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

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
	
	public String getText(){		
		IDocumentProvider dp = editor.getDocumentProvider();
        final IDocument doc = dp.getDocument(editor.getEditorInput());
        return doc.get();
	}
}
