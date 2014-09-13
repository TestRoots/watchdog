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

	@Test
	public void testIsEmpty() {
		assertEquals(true, WatchDogUtils.isEmpty(null));
		assertEquals(true, WatchDogUtils.isEmpty(""));
		assertEquals(false, WatchDogUtils.isEmpty(" "));
	}

	@Test
	public void testIsEmptyOrHasOnlyWhitespaces() {
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(null));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(""));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(" "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("      "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  	\n  "));
		assertEquals(false, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("a"));
		assertEquals(false,
				WatchDogUtils.isEmptyOrHasOnlyWhitespaces("   f   "));
	}

	@Test
	public void testCountSLOC() {
		assertEquals(0, WatchDogUtils.countSLOC(""));
		assertEquals(0, WatchDogUtils.countSLOC("\n\n"));
		assertEquals(0, WatchDogUtils.countSLOC("    \n	\n      "));

		assertEquals(1, WatchDogUtils.countSLOC("One line"));
		assertEquals(1, WatchDogUtils.countSLOC("One line\n"));
		assertEquals(1, WatchDogUtils.countSLOC("\n\nOne line\n"));
		assertEquals(1, WatchDogUtils.countSLOC("\n   \nOne line\n"));

		assertEquals(2, WatchDogUtils.countSLOC("Two\nlines."));

		assertEquals(2, WatchDogUtils.countSLOC("Two\r\nlines."));
		assertEquals(2, WatchDogUtils.countSLOC("Also two\r\n\r\nlines."));
		assertEquals(3, WatchDogUtils.countSLOC("Now\r\nthree\r\nlines."));
	}

	@Test
	public void testHashFileName() {
		String expectedHash = "a6bb3545c5d1424e8bb6e95aceb1c734535e7ca3";
		assertEquals(expectedHash, WatchDogUtils.createFileNameHash("AClass"));
		assertEquals(expectedHash,
				WatchDogUtils.createFileNameHash("AClass.java"));
		assertEquals(expectedHash + "Test",
				WatchDogUtils.createFileNameHash("AClassTest.java"));

	}

}
