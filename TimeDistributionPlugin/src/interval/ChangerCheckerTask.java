package interval;

import java.util.TimerTask;

import org.eclipse.ui.texteditor.ITextEditor;

public class ChangerCheckerTask extends TimerTask {
	
	private UpdateChecker checker;
	public ChangerCheckerTask(ITextEditor editor){
		checker = new UpdateChecker(editor);
	}
	
	
	@Override
	public void run() {
		if(checker.hasChanged()){
			System.out.println("was changed!");			
		}else{
			System.out.println("inactive now!");
			this.cancel();
		}
			
	}
}
