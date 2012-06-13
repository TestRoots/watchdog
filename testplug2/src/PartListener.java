import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;


public class PartListener implements IPartListener{

	@Override
	public void partOpened(IWorkbenchPart part) {
		if(part instanceof ITextEditor){		
			System.out.println("partOpened");
			getEditorContent(part);
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
			getEditorContent(part);
		}
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		if(part instanceof ITextEditor){
			System.out.println("partActivated");
			getEditorContent(part);
		}
		
	}

	private String getEditorContent(IWorkbenchPart part){
		if(part instanceof ITextEditor){		
			ITextEditor editor = (ITextEditor)part;
	        IDocumentProvider dp = editor.getDocumentProvider();
	        IDocument doc = dp.getDocument(editor.getEditorInput());
	        doc.addDocumentListener(new IDocumentListener() {
				
				@Override
				public void documentChanged(DocumentEvent event) {
					System.out.println("documentChanged");
				}
				
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
					System.out.println("documentAboutToBeChanged");
				}
			});
	        String s = doc.get();
	        System.out.println(s);
	        return s;
		}
		throw new IllegalArgumentException(part.toString() + " is not an TextEditor part");
	}
	
}
