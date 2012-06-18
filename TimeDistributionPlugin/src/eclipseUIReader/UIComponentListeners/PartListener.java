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


public class PartListener implements IPartListener{
	private static List<ITextEditor> listeningEditors = new LinkedList<ITextEditor>();

	@Override
	public void partOpened(IWorkbenchPart part) {
		System.out.println("partOpened");
		try{
			addDocumentListener(part);		
		}catch(IllegalArgumentException ex){
			MyLogger.logInfo("Ignored part "+part.getTitle()+", was not an editor");			
		}
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if(part instanceof ITextEditor){
			System.out.println("partDeactivated");
		}
	}
	
	@Override
	public void partClosed(IWorkbenchPart part) {
		if(part instanceof ITextEditor){
			System.out.println("partClosed");
		}
	}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		if(part instanceof ITextEditor){
			System.out.println("partBroughtToTop");
		}
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		if(part instanceof ITextEditor){
			System.out.println("partActivated");
			addDocumentListener(part);
		}
		
	}
	
	public static void addDocumentListener(IWorkbenchPart part){
		if(!(part instanceof ITextEditor))
			throw new IllegalArgumentException("Opened part was not an instance of ITextEditor, but of: "+part.toString());
		
		ITextEditor editor = (ITextEditor)part;        
		if(!listeningEditors.contains(editor)){
			IDocumentProvider dp = editor.getDocumentProvider();
	        final IDocument doc = dp.getDocument(editor.getEditorInput());
	        doc.addDocumentListener(new IDocumentListener() {
				
				@Override
				public void documentChanged(DocumentEvent event) {
					String s = doc.get();
			        System.out.println(s);
				}
				
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
					//System.out.println("documentAboutToBeChanged");
				}
			});
	        listeningEditors.add(editor);			
		}
	}
	
}
