package nl.tudelft.watchdog.interval.recorded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;

public class RecordedIntervalSerializationManager {
	
	public static void saveRecordedIntervals(){
		if(!IntervalKeeper.getInstance().getRecordedIntervals().isEmpty()){
			try
			{
				String filename = (new Date()).getTime() + ".ser";
				File parent = new File("watchdog/");
				parent.mkdirs();
				FileOutputStream fileOut = new FileOutputStream(new File(parent, filename));
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
	}

	@SuppressWarnings("unchecked")
	public static List<IInterval> retrieveRecordedIntervals() throws IOException, ClassNotFoundException{
		List<IInterval> completeList = new ArrayList<IInterval>();
		try
		{
			File parent = new File("watchdog/");
			
			for(String fileName : parent.list()){
				File f = new File(parent, fileName);
				
				if(f.exists() && f.isFile()){			
					FileInputStream fileIn = new FileInputStream(f);
					
					ObjectInputStream in = new ObjectInputStream(fileIn);
					List<IInterval> list = (List<IInterval>) in.readObject();
					in.close();
					fileIn.close();
					completeList.addAll(list);
				}
				else
				{
					MyLogger.logInfo("no saved recorded intervals");
				}
			}
			return completeList;
		}
		catch(IOException e)
		{
			MyLogger.logSevere(e);
			throw e;
		} 
		catch (ClassNotFoundException e) {
			MyLogger.logSevere(e);
			throw e;
		}
	}
}
