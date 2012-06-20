package interval;

import interval.ChangerCheckerTask.RunCallBack;

import java.util.Timer;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;

public class ActiveInterval {
	private Timer checkForChangeTimer;
	private ITextEditor editor;
	/**
	 * 
	 * @param editor
	 * 		the editor in this interval
	 * @param timeout
	 * 		in millisecond
	 */
	public ActiveInterval(ITextEditor editor){
		this.editor = editor;
		checkForChangeTimer = new Timer();
	}
	
	public void start(long timeout, RunCallBack callbackWhenFinished){
		checkForChangeTimer.schedule(new ChangerCheckerTask(editor, callbackWhenFinished), timeout, timeout);
	}
	
	public ITextEditor getEditor(){
		return editor;
	}
	public Timer getTimer(){
		return checkForChangeTimer;
	}
}
