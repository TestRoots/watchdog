import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;



public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (activeWindow != null)
		{
			System.out.println("activeWindow");
			IWorkbenchPage activePage = activeWindow.getActivePage();

		    if (activePage != null)
		    {
		    	System.out.println("activePage");
		    	activePage.addPartListener(new PartListener());
		    }
		    else
		    {
		    	System.out.println("NOTactivePage");
		        activeWindow.addPageListener(new PageListener());
		    }
		}
		else
		{
			System.out.println("NOTactiveWindow");
			for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
		    {
		        for (IWorkbenchPage page : window.getPages()) {
		            if(page.getActivePart() != null) System.out.println("derp");
		            IWorkbenchPart part = page.getActivePart();
		            //System.out.println(ISaveablePart.PROP_DIRTY); //prop dirty = not saved doc
		            if(part != null) System.out.println("herp");
		            if(part instanceof IEditorPart) {
		            	System.out.println("editor activated");
		            	PartListener.addDocumentListener(part);
		            }
		            
		        	page.addPartListener(new PartListener());
		        }
		        window.addPageListener(new PageListener());
		    }

		    PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
				
		    	@Override
				public void windowOpened(IWorkbenchWindow window) {
					System.out.println("windowOpened");
				}
				@Override
				public void windowDeactivated(IWorkbenchWindow window) {
					System.out.println("windowDeactivated");
				}

				@Override
				public void windowClosed(IWorkbenchWindow window) {
					System.out.println("windowClosed");
				}

				@Override
				public void windowActivated(IWorkbenchWindow window) {
					System.out.println("windowActivated");
				}
			});
		}      
	}
}			
		
		

