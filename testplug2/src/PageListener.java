import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;


public class PageListener implements IPageListener {
	
	@Override
	public void pageOpened(IWorkbenchPage page) {
		System.out.println("pageOpened");
		
		//page.addPartListener(new PartListener());
	}
	
	@Override
	public void pageClosed(IWorkbenchPage page) {
		System.out.println("pageClosed");
	}
	
	@Override
	public void pageActivated(IWorkbenchPage page) {
		System.out.println("pageActivated");
	}
}


