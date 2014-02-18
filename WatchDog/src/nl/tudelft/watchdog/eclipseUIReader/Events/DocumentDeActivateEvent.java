package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventObject;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("serial")
public class DocumentDeActivateEvent extends EventObject {
	ITextEditor editor;
	IWorkbenchPart part;
	
	public DocumentDeActivateEvent(IWorkbenchPart part) {
		super(part);
		this.part = part;
		this.editor = (ITextEditor) part;	
	}

	public IWorkbenchPart getPart(){
		return part;
	}
	
	public ITextEditor getChangedEditor(){
		return editor;
	}
}
