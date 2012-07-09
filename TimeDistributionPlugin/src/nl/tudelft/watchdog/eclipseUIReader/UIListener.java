package nl.tudelft.watchdog.eclipseUIReader;

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
		
		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			
			@Override
			public boolean preShutdown(IWorkbench workbench, boolean forced) {
				RecordedIntervalSerializationManager.saveRecordedIntervals();
				return true;
			}
			
			@Override
			public void postShutdown(IWorkbench workbench) {}
		});
		
		//for new windows
		PlatformUI.getWorkbench().addWindowListener(new WindowListener());
		
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
	    {
			//for existing windows
			WindowListener.addPageListener(window); 
	    }
		
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
	    {
			IWorkbenchPage activePage = window.getActivePage();
			if(activePage != null){
				IWorkbenchPart activePart = activePage.getActivePart();
				if(activePart instanceof ITextEditor)
					DocChangeListenerAttacher.listenToDocChanges(activePart);		
			} 
		}
				
	}   
}  