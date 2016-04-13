package nl.tudelft.watchdog.core.ui.util;

import java.awt.Color;
import java.awt.Paint;

/**
 * Utility class that returns the right color for an event to be used inside the
 * GanttChart of the WatchDogView.
 */
public class DebugEventColors {

	/**
	 * @return the {@link Color} belonging to debug event <number>
	 */
	public static Paint get(int number) {
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

}
