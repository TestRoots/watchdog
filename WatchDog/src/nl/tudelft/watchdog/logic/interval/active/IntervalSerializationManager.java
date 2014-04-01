package nl.tudelft.watchdog.logic.interval.active;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

public class IntervalSerializationManager {

	public void saveRecordedIntervals() {
		if (!IntervalManager.getInstance().getRecordedIntervals().isEmpty()) {
			try {
				String filename = (new Date()).getTime() + ".ser";
				// TODO (MMB) This stores serialized files in user's home.
				// Change to a more appropriate location or, better yet, use
				// Eclipse's internal mechanism for storing such data?
				String userHome = System.getProperty("user.home");
				File parent = new File(userHome + "/watchdog/");
				parent.mkdirs();
				FileOutputStream fileOut = new FileOutputStream(new File(
						parent, filename));
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(IntervalManager.getInstance()
						.getRecordedIntervals());
				out.close();
				fileOut.close();
			} catch (IOException e) {
				WatchDogLogger.logSevere(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<IntervalBase> retrieveRecordedIntervals()
			throws IOException, ClassNotFoundException {
		List<IntervalBase> completeList = new ArrayList<IntervalBase>();
		try {
			String userHome = System.getProperty("user.home");
			File parent = new File(userHome + "/watchdog/");
			if (parent.list() != null) {
				for (String fileName : parent.list()) {
					File file = new File(parent, fileName);

					if (file.exists() && file.isFile()) {
						FileInputStream fileIn = new FileInputStream(file);

						ObjectInputStream inputStream = new ObjectInputStream(
								fileIn);
						List<IntervalBase> list = (List<IntervalBase>) inputStream
								.readObject();
						inputStream.close();
						fileIn.close();
						completeList.addAll(list);
					} else {
						WatchDogLogger.logInfo("no saved recorded intervals");
					}
				}
			}
			return completeList;
		} catch (IOException e) {
			WatchDogLogger.logSevere(e);
			throw e;
		} catch (ClassNotFoundException e) {
			WatchDogLogger.logSevere(e);
			throw e;
		}
	}
}
