package nl.tudelft.watchdog.interval.recorded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MessageConsoleManager;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;

public class RecordedIntervalSerializationManager {
	private final static String fileName = "recordedIntervals.ser";
	
	public static void saveRecordedIntervals(){
		try
		{
			FileOutputStream fileOut =
			new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(IntervalKeeper.getInstance().getRecordedIntervals());
			out.close();
			fileOut.close();
		}
		catch(IOException e)
		{
          MyLogger.logSevere(e);
		}		
	}

	@SuppressWarnings("unchecked")
	public static void retrieveRecordedIntervals(){
		List<IInterval> list = null;
		try
		{
			File f = new File(fileName);
			if(f.exists()){			
				FileInputStream fileIn = new FileInputStream(new File("recordedIntervals.ser"));
				
				ObjectInputStream in = new ObjectInputStream(fileIn);
				list = (List<IInterval>) in.readObject();
				in.close();
				fileIn.close();
				
				IntervalKeeper.getInstance().setRecordedIntervals(list);
			}else
				MessageConsoleManager.getConsoleStream().println("no saved recorded intervals");
				MyLogger.logInfo("no saved recorded intervals");
		}
		catch(IOException e)
		{
			MyLogger.logSevere(e);
		} 
		catch (ClassNotFoundException e) {
			MyLogger.logSevere(e);
		}
	}
}
