package nl.tudelft.watchdog.timeDistributionPlugin.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nl.tudelft.watchdog.interval.IInterval;
import nl.tudelft.watchdog.interval.IIntervalKeeper;
import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MessageConsoleManager;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;
import nl.tudelft.watchdog.timingOutput.IIntervalWriter;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.MessageConsoleStream;


public class ExportHandler extends AbstractHandler{

	private MessageConsoleStream stream;
	private IIntervalWriter intervalWriter;
	
	public ExportHandler() {
		stream = MessageConsoleManager.getConsoleStream();
		intervalWriter = new IntervalsToXMLWriter();
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		stream.println("Wroof!");		
		
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		for(IInterval interval : intervalKeeper.getRecordedIntervals()){
			 stream.println(interval.getDocument().getFileName() +"\t\t" + interval.getDurationString()+ "\t\t" + interval.getStart()+" - "+interval.getEnd());			 
		}
		saveToFile();
		return null;
	}
	
	private void saveToFile(){
		Display display = Display.getCurrent();
	    Shell shell = new Shell(display);
	    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
	    dialog.setFilterNames(new String[] { "XML files", "All Files (*.*)" });
	    dialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); // Windows
	                                    // wild
	                                    // cards
	    dialog.setFilterPath("c:\\"); // Windows path
	    dialog.setFileName("WatchDogIntervals.xml");
	    
	    String path = dialog.open();
	    
	    File f = new File(path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			intervalWriter.exportIntervals(IntervalKeeper.getInstance().getRecordedIntervals(), fos);
		} catch (FileNotFoundException e) {
			//TODO: some error handling here
			MyLogger.logSevere(e);
		}
	}
}   