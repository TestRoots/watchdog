package nl.tudelft.watchdog.interval.activityCheckers;

import nl.tudelft.watchdog.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.util.WatchDogUtil;

import org.eclipse.ui.texteditor.ITextEditor;


public class UpdateChecker implements IUpdateChecker{

	private ITextEditor editor;
	private String previousContent;
	private String lastCheckedContent;
	
	public UpdateChecker(ITextEditor editor){
		this.editor = editor;
		this.previousContent = WatchDogUtil.getEditorContent(editor);
	}
	
	@Override
	public boolean hasChanged() throws EditorClosedPrematurelyException {
		try{
			lastCheckedContent = WatchDogUtil.getEditorContent(editor);
		}catch(IllegalArgumentException ex){
			throw new EditorClosedPrematurelyException();
		}
		boolean isChanged = !previousContent.equals(lastCheckedContent);	
		previousContent = lastCheckedContent;
		return isChanged;
	}

}
