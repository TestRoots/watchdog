import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;


public class PartListener implements IPartListener{

	@Override
	public void partOpened(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		System.out.println("partOpened");
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		System.out.println("partDeactivated");
		
	}
	
	@Override
	public void partClosed(IWorkbenchPart part) {
		System.out.println("partClosed");
		
	}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		System.out.println("partBroughtToTop");
		
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		System.out.println("partActivated");
	}

}
