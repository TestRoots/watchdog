package eclipseUIReader.UIComponentListeners;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import timeDistributionPlugin.logging.MyLogger;
import eclipseUIReader.DocChangeListenerAttacher;
import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;



public class PartListener implements IPartListener{
	@Override
	public void partOpened(IWorkbenchPart part) {
		try{
			DocChangeListenerAttacher.listenToDocChanges(part);
		}catch(IllegalArgumentException ex){
			MyLogger.logInfo("Ignored part "+part.getTitle()+", was not an editor");
		}
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		
	}
	
	@Override
	public void partClosed(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor)part;
			DocumentNotifier.fireDocumentDeactivatedEvent(new DocumentAttentionEvent(editor));
		}
	}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		try{
			DocChangeListenerAttacher.listenToDocChanges(part);
		}catch(IllegalArgumentException ex){
			MyLogger.logInfo("Ignored part "+part.getTitle()+", was not an editor");
		}
	}

	
	
}
