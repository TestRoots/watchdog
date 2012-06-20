package eclipseUIReader;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;
import eclipseUIReader.UIComponentListeners.WindowListener;


public class UIListener extends DocumentNotifier {
	public void attachListeners(){		
		
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
				if(activePart instanceof ITextEditor){				
					DocumentNotifier.fireDocumentActivatedEvent(new DocumentAttentionEvent((ITextEditor) activePart));
				}
			}
		}
				
	}
}
