package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventObject;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("serial")
public class DocumentActivateEvent extends EventObject {
	ITextEditor editor;
	IWorkbenchPart part;

	public DocumentActivateEvent(IWorkbenchPart part) {
		super(part);
		this.part = part;
		this.editor = (ITextEditor) part;
	}

	public IWorkbenchPart getPart() {
		return part;
	}

	public ITextEditor getChangedEditor() {
		return editor;
	}

}
