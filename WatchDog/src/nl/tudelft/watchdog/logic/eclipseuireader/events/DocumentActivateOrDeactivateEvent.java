package nl.tudelft.watchdog.logic.eclipseuireader.events;

import java.util.EventObject;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("serial")
public class DocumentActivateOrDeactivateEvent extends EventObject {
	ITextEditor editor;
	IWorkbenchPart part;

	public DocumentActivateOrDeactivateEvent(IWorkbenchPart part) {
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
