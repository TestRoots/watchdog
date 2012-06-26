package eclipseUIReader;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;

public class DocChangeListenerAttacher {
	private static List<IWorkbenchPart> handlers = new LinkedList<IWorkbenchPart>();
	
	public static void listenToDocChanges(final IWorkbenchPart part) throws IllegalArgumentException {
		if(!handlers.contains(part)){
			if(part instanceof ITextEditor)
			{
				handlers.add(part);
				final ITextEditor editor = (ITextEditor)part;
				
				IDocumentProvider dp = editor.getDocumentProvider();
		        final IDocument doc = dp.getDocument(editor.getEditorInput());
		        doc.addDocumentListener(new IDocumentListener() {
					
					@Override
					public void documentChanged(DocumentEvent event) {
						DocumentNotifier.fireDocumentActivatedEvent(new DocumentAttentionEvent(editor));
						doc.removeDocumentListener(this); //just listen 1 time for this event to prevent overflow of events
						handlers.remove(part);
					}
					
					@Override
					public void documentAboutToBeChanged(DocumentEvent event) {}
				});			
			}else{
				throw new IllegalArgumentException("part was not instance of ITextEditor");
			}
		}		
	}
}
