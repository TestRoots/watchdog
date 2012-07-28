package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventObject;

import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("serial")
public class DocumentActivateEvent extends EventObject {
	ITextEditor editor;
	
	public DocumentActivateEvent(ITextEditor editor) {
		super(editor);
		this.editor = editor;
	}

	public ITextEditor getChangedEditor(){
		return editor;
	}
	
}
