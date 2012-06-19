package eclipseUIReader.UIComponentListeners;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

public class WindowListener implements IWindowListener {
	@Override
	public void windowOpened(IWorkbenchWindow window) {
		addPageListener(window);
	}
	@Override
	public void windowDeactivated(IWorkbenchWindow window) {}

	@Override
	public void windowClosed(IWorkbenchWindow window) {}

	@Override
	public void windowActivated(IWorkbenchWindow window) {}
	
	public static void addPageListener(IWorkbenchWindow window){
		//for new pages added in this window
		window.addPageListener(new PageListener());
		
		//for existing pages in this window
		for (IWorkbenchPage page : window.getPages()) {	        	
            PageListener.addPartListener(page);
        }	
	}
}
