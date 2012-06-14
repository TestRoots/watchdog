import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;


public class PartListener implements IPartListener{
	private static List<IWorkbenchPart> editors = new LinkedList<IWorkbenchPart>();

	@Override
	public void partOpened(IWorkbenchPart part) {
		if(part instanceof ITextEditor){		
			System.out.println("partOpened");
			addDocumentListener(part);
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
		assert part instanceof ITextEditor;
		
		if(!editors.contains(part)){
			ITextEditor editor = (ITextEditor)part;
	        IDocumentProvider dp = editor.getDocumentProvider();
	        final IDocument doc = dp.getDocument(editor.getEditorInput());
	        doc.addDocumentListener(new IDocumentListener() {
				
				@Override
				public void documentChanged(DocumentEvent event) {
					System.out.println("documentChanged");
					String s = doc.get();
			        System.out.println(s);
				}
				
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
					//System.out.println("documentAboutToBeChanged");
				}
			});
	        editors.add(part);
	        System.out.println("listener added");			
		}
	}
	
}
