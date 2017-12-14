package nl.tudelft.watchdog.core.ui.util;

import java.awt.Color;
import java.awt.Paint;
import java.text.SimpleDateFormat;
import java.util.List;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;

/**
 * Utility class for visualizing the debug events inside the GanttChart of the
 * WatchDogView.
 */
public class DebugEventVisualizationUtils {

	/**
	 * @return the right {@link Color} belonging to debug event <number> to be
	 *         used inside the GanttChart of the WatchDogView/
	 */
	public static Paint getColorForNumber(int number) {
		switch (number) {
		case 0:
			return Color.red;
		case 1:
			return Color.blue;
		case 2:
			return Color.green;
		case 3:
			return Color.yellow;
		case 4:
			return Color.black;
		case 5:
			return Color.cyan;
		case 6:
			return Color.darkGray;
		case 7:
			return Color.gray;
		case 8:
			return new Color(118, 168, 255);
		case 9:
			return Color.magenta;
		case 10:
			return Color.orange;
		case 11:
			return Color.pink;
		case 12:
			return Color.white;
		default:
			return Color.red;
		}
	}

	/** @return the string representations of the lastestDebugIntervals. */
	public static String[] getDebugIntervalStrings(List<DebugInterval> latestDebugIntervals) {
		String[] debugIntervalStrings = new String[latestDebugIntervals.size()];
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM d HH:mm:ss");
		for (int i = 0; i < latestDebugIntervals.size(); i++) {
			DebugInterval currentInterval = latestDebugIntervals.get(i);
			debugIntervalStrings[i] = dateFormatter.format(currentInterval.getStart()) + " - "
					+ dateFormatter.format(currentInterval.getEnd());
		}
		return debugIntervalStrings;
	}

}
