package nl.tudelft.watchdog.logic.interval;

import java.io.OutputStream;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.recorded.IInterval;

public interface IIntervalWriter {

	/**
	 * Prints intervals in XML format to an output stream
	 * 
	 * @param intervals
	 * @param file
	 */
	public abstract void exportIntervals(List<IInterval> intervals,
			OutputStream stream);

}