package nl.tudelft.watchdog.logic.interval.recorded;

import java.io.IOException;
import java.util.List;

public interface IRecordedIntervalSerializationManager {

	void saveRecordedIntervals();

	List<IInterval> retrieveRecordedIntervals() throws IOException,
			ClassNotFoundException;

}