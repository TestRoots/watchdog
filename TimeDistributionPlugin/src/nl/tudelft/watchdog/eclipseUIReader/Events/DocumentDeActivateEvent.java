package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventObject;

import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("serial")
public class DocumentDeActivateEvent extends EventObject {
	ITextEditor editor;
	
	public DocumentDeActivateEvent(ITextEditor editor) {
		super(editor);
		this.editor = editor;	
	}

	public ITextEditor getChangedEditor(){
		return editor;
	}
}
