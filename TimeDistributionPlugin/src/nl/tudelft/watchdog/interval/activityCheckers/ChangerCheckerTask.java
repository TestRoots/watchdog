package nl.tudelft.watchdog.interval.activityCheckers;

import java.util.TimerTask;

import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentAttentionEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentNotifier;
import nl.tudelft.watchdog.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;



public class ChangerCheckerTask extends TimerTask {
	
	private IUpdateChecker checker;
	private RunCallBack callback;
	private ITextEditor editor;
	public ChangerCheckerTask(ITextEditor editor, RunCallBack callback){
		checker = new UpdateChecker(editor);
		this.editor = editor;
		this.callback = callback;
	}
	
	
	@Override
	public void run() {
		try {
			if(checker.hasChanged()){
				//still an active document		
			}else{
				//not active anymore
				this.cancel();//stop timer
				listenForChanges();//listen to changes in open, inactive documents
				callback.onInactive();//callback function			
			}
		} catch (EditorClosedPrematurelyException e) {
			MyLogger.logInfo("Editor closed prematurely"); //this can happen when eclipse is closed while the document is still active
		}
			
	}
	
	private void listenForChanges(){
		IDocumentProvider dp = editor.getDocumentProvider();
        final IDocument doc = dp.getDocument(editor.getEditorInput());
        
        final IDocumentListener docListener = new IDocumentListener() {
			
			@Override
			public void documentChanged(DocumentEvent event) {
				//listen to this event just once, notify that the document is activated, then remove this listener
				DocumentNotifier.fireDocumentActivatedEvent(new DocumentAttentionEvent(editor));
				doc.removeDocumentListener(this);
			}
			
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {}
		};
        
        doc.addDocumentListener(docListener);
	}
}
