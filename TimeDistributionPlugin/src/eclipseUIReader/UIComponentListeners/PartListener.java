package eclipseUIReader.UIComponentListeners;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import timeDistributionPlugin.MyLogger;
import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;



public class PartListener implements IPartListener{
	@Override
	public void partOpened(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor)part;
			DocumentNotifier.fireDocumentActivatedEvent(new DocumentAttentionEvent(editor));
		}else{
			MyLogger.logInfo("Ignored part "+part.getTitle()+", was not an editor");			
		}
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor)part;
			DocumentNotifier.fireDocumentDeactivatedEvent(new DocumentAttentionEvent(editor));
		}
	}
	
	@Override
	public void partClosed(IWorkbenchPart part) {}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor)part;
			DocumentNotifier.fireDocumentActivatedEvent(new DocumentAttentionEvent(editor));
		}
	}
	
}
