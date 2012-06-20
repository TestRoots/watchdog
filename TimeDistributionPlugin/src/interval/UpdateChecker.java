package interval;

import org.eclipse.ui.texteditor.ITextEditor;

import exceptions.EditorClosedPrematurelyException;

import timeDistributionPlugin.MyLogger;
import util.TextEditorContentReader;

public class UpdateChecker{

	private ITextEditor editor;
	private String previousContent;
	private String lastCheckedContent;
	
	public UpdateChecker(ITextEditor editor){
		this.editor = editor;
		this.previousContent = TextEditorContentReader.getEditorContent(editor);
	}
	
	public boolean hasChanged() throws EditorClosedPrematurelyException {
		try{
			lastCheckedContent = TextEditorContentReader.getEditorContent(editor);
		}catch(IllegalArgumentException ex){
			throw new EditorClosedPrematurelyException();
		}
		boolean isChanged = !previousContent.equals(lastCheckedContent);	
		previousContent = lastCheckedContent;
		return isChanged;
	}

}
