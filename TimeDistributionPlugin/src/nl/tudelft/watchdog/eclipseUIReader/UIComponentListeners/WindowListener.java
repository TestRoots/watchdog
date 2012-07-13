package nl.tudelft.watchdog.eclipseUIReader.UIComponentListeners;

import nl.tudelft.watchdog.timeDistributionPlugin.logging.MessageConsoleManager;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

public class WindowListener implements IWindowListener {
	@Override
	public void windowOpened(IWorkbenchWindow window) {
		MessageConsoleManager.getConsoleStream().println("windowOpened");
		addPageListener(window);
		
	}
	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		MessageConsoleManager.getConsoleStream().println("windowDeactivated");
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		MessageConsoleManager.getConsoleStream().println("windowClosed");
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		MessageConsoleManager.getConsoleStream().println("windowActivated");
	}
	
	public static void addPageListener(IWorkbenchWindow window){
		//for new pages added in this window
		window.addPageListener(new PageListener());
		
		//for existing pages in this window
		for (IWorkbenchPage page : window.getPages()) {	  
			PageListener.addPartListener(page);
        }	
	}
}
 