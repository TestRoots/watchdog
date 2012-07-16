package nl.tudelft.watchdog.timeDistributionPlugin.prompts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import nl.tudelft.watchdog.exceptions.FileSavingFailedException;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;
import nl.tudelft.watchdog.timingOutput.IIntervalWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UserPrompter {
	
	private static Display display;
	private static Shell shell;
	
	static{
		display = Display.getCurrent();
	    shell = new Shell(display);
	}
	
	public static void saveIntervalsToFile(IIntervalWriter writer, List<IInterval> intervals) throws FileSavingFailedException{		
	    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
	    dialog.setFilterNames(new String[] { "XML files", "All Files (*.*)" });
	    dialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); // Windows	wild cards
	    dialog.setFilterPath("c:/"); // Windows path
	    dialog.setFileName("WatchDogIntervals.xml");
	    dialog.setOverwrite(true);

	    String path = dialog.open();
	    
	    if(path != null){
		    File f = new File(path);
			try {
				FileOutputStream fos = new FileOutputStream(f);
				writer.exportIntervals(intervals, fos);
			} catch (FileNotFoundException e) {
				MyLogger.logSevere(e);
				throw new FileSavingFailedException(e);				
			}
	    }else{
	    	MyLogger.logInfo("File saving canceled");
	    }
	}

	public static void showMessageBox(String title, String contents) {
		MessageBox b = new MessageBox(shell);	    
	    b.setText(title);
	    b.setMessage(contents);	    
	    b.open();
	}
}
