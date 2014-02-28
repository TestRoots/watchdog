package nl.tudelft.watchdog.util;

import static org.junit.Assert.assertEquals;

import org.joda.time.Duration;
import org.junit.Test;

/**
 * Tests for the {@link WatchDogUtils}.
 */
public class WatchDogUtilsTest {

	/**
	 * Tests the human readable durations.
	 */
	@Test
	public void testConvertJodaDurationToReadableString() {
		Duration duration = new Duration(0);
		assertEquals("0 seconds",
				WatchDogUtils.makeDurationHumanReadable(duration));

		duration = new Duration(999);
		assertEquals("0 seconds",
				WatchDogUtils.makeDurationHumanReadable(duration));

		duration = new Duration(1000);
		assertEquals("1 second",
				WatchDogUtils.makeDurationHumanReadable(duration));

		duration = new Duration(1010);
		assertEquals("1 second",
				WatchDogUtils.makeDurationHumanReadable(duration));

		duration = new Duration(1999);
		assertEquals("1 second",
				WatchDogUtils.makeDurationHumanReadable(duration));

		duration = new Duration(2034);
		assertEquals("2 seconds",
				WatchDogUtils.makeDurationHumanReadable(duration));

		duration = new Duration(11111111L);
		assertEquals("3 hours, 5 minutes and 11 seconds",
				WatchDogUtils.makeDurationHumanReadable(duration));

		duration = new Duration(848654688715648L);
		assertEquals("6 days, 5 hours, 31 minutes and 55 seconds",
				WatchDogUtils.makeDurationHumanReadable(duration));
	}

}
