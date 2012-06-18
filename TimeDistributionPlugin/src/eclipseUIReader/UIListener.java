package eclipseUIReader;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import eclipseUIReader.UIComponentListeners.WindowListener;


public class UIListener {
	public void attachListeners(){		
		
		//for new windows
		PlatformUI.getWorkbench().addWindowListener(new WindowListener());
		
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
	    {
			//for existing windows
			WindowListener.addPageListener(window);	        
	    }     
	}
}
