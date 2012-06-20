package interval;

import org.eclipse.ui.texteditor.ITextEditor;

import util.TextEditorContentReader;

public class UpdateChecker{

	private ITextEditor editor;
	private String previousContent;
	private String lastCheckedContent;
	
	public UpdateChecker(ITextEditor editor){
		this.editor = editor;
		this.previousContent = TextEditorContentReader.getEditorContent(editor);
	}
	
	public boolean hasChanged(){
		lastCheckedContent = TextEditorContentReader.getEditorContent(editor);
		boolean isChanged = !previousContent.equals(lastCheckedContent);	
		previousContent = lastCheckedContent;
		return isChanged;
	}

}
