package nl.tudelft.watchdog.logic.interval.active;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

public class IntervalSerializationManager {

	public void saveRecordedIntervals() {
		if (!IntervalManager.getInstance().getClosedIntervals().isEmpty()) {
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

				// out.writeObject(IntervalManager.getInstance()
				// .getClosedIntervals());
				out.close();
				fileOut.close();
			} catch (IOException e) {
				WatchDogLogger.getInstance().logSevere(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<IntervalBase> retrieveRecordedIntervals() throws IOException,
			ClassNotFoundException {
		// TODO (MMB) change to where level db is located
		List<IntervalBase> completeList = new ArrayList<IntervalBase>();
		return completeList;
	}
}
