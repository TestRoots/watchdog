package eclipseUIReader.UIComponentListeners;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import timeDistributionPlugin.MyLogger;
import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;



public class PartListener extends DocumentNotifier implements IPartListener{
	private static List<ITextEditor> listeningEditors = new LinkedList<ITextEditor>();

	@Override
	public void partOpened(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor)part;
			fireMyEvent(new DocumentAttentionEvent(editor));
			addDocumentListener(part);		
		}else{
			MyLogger.logInfo("Ignored part "+part.getTitle()+", was not an editor");			
		}
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {}
	
	@Override
	public void partClosed(IWorkbenchPart part) {}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {}
	
	@Override
	public void partActivated(IWorkbenchPart part) {}
	
	static void addDocumentListener(IWorkbenchPart part){
		if(!(part instanceof ITextEditor))
			throw new IllegalArgumentException("Opened part was not an instance of ITextEditor, but of: "+part.toString());
		
		final ITextEditor editor = (ITextEditor)part;        
		if(!listeningEditors.contains(editor)){
			IDocumentProvider dp = editor.getDocumentProvider();
	        final IDocument doc = dp.getDocument(editor.getEditorInput());
	        doc.addDocumentListener(new IDocumentListener() {
				
				@Override
				public void documentChanged(DocumentEvent event) {
					DocumentNotifier.fireMyEvent(new DocumentAttentionEvent(editor));					
				}
				
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {}
			});
	        listeningEditors.add(editor);			
		}		
	}
	
	
}
