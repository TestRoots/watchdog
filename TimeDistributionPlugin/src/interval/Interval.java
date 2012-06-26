package interval;

import org.eclipse.ui.texteditor.ITextEditor;

public abstract class Interval implements IInterval {
	protected ITextEditor editor;
	
	public Interval(ITextEditor editor){
		this.editor = editor;
	}
	
	@Override
	public ITextEditor getEditor(){
		return editor;
	}
}
