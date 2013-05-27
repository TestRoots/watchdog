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
import nl.tudelft.watchdog.plugin.logging.WDLogger;

public class RecordedIntervalSerializationManager implements IRecordedIntervalSerializationManager {
	
	@Override
	public void saveRecordedIntervals(){
		if(!IntervalKeeper.getInstance().getRecordedIntervals().isEmpty()){
			try
			{
				String filename = (new Date()).getTime() + ".ser";
				String userHome = System.getProperty("user.home");
				File parent = new File(userHome+"/watchdog/");
				parent.mkdirs();
				FileOutputStream fileOut = new FileOutputStream(new File(parent, filename));
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(IntervalKeeper.getInstance().getRecordedIntervals());
				out.close();
				fileOut.close();
			}
			catch(IOException e)
			{
	          WDLogger.logSevere(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IInterval> retrieveRecordedIntervals() throws IOException, ClassNotFoundException{
		List<IInterval> completeList = new ArrayList<IInterval>();
		try
		{
			String userHome = System.getProperty("user.home");
			File parent = new File(userHome+"/watchdog/");
			if(parent.list() != null){
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
						WDLogger.logInfo("no saved recorded intervals");
					}
				}
			}
			return completeList;
		}
		catch(IOException e)
		{
			WDLogger.logSevere(e);
			throw e;
		} 
		catch (ClassNotFoundException e) {
			WDLogger.logSevere(e);
			throw e;
		}
	}
}
