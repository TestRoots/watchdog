package nl.tudelft.watchdog.eclipseUIReader;

import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentAttentionEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentNotifier;
import nl.tudelft.watchdog.eclipseUIReader.UIComponentListeners.WindowListener;
import nl.tudelft.watchdog.interval.RecordedIntervalSerializationManager;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * class that sets up the listeners for eclipse UI events
 */
public class UIListener implements IUIListener {
	@Override
	public void attachListeners(){
		addShutdownListeners();
		
		PlatformUI.getWorkbench().addWindowListener(new WindowListener());
		
		addListenersToAlreadyOpenWindows();				
	}

	private void addShutdownListeners() {
		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			
			@Override
			public boolean preShutdown(final IWorkbench workbench, final boolean forced) {
				RecordedIntervalSerializationManager.saveRecordedIntervals();
				return true;
			}
			
			@Override
			public void postShutdown(final IWorkbench workbench) {}
		});
	}

	private void addListenersToAlreadyOpenWindows() {
		for (final IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
	    {
			WindowListener.addPageListener(window);
			IWorkbenchPage activePage = window.getActivePage();
			
			if(activePage != null){
				final IWorkbenchPart activePart = activePage.getActivePart();
				if(activePart instanceof ITextEditor)	
				{
					DocumentNotifier.fireDocumentStartFocusEvent(new DocumentAttentionEvent((ITextEditor)activePart));
					DocChangeListenerAttacher.listenToDocChanges(activePart);
				}
			} 
		}
	}   
}  