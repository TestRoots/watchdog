package nl.tudelft.watchdog.eclipseUIReader.UIComponentListeners;
import nl.tudelft.watchdog.eclipseUIReader.DocChangeListenerAttacher;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentAttentionEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentNotifier;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;




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
