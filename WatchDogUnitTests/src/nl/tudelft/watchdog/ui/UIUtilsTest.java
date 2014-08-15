package nl.tudelft.watchdog.ui;

import static org.junit.Assert.*;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.junit.Test;

public class UIUtilsTest {

	@Test
	public void testIsEmpty() {
		assertEquals(true, WatchDogUtils.isEmpty(""));
		assertEquals(true, WatchDogUtils.isEmpty(null));

		assertEquals(false, WatchDogUtils.isEmpty(" "));
		assertEquals(false, WatchDogUtils.isEmpty("ffanynormalstring"));
	}

	@Test
	public void testIsEmptySansWhitespace() {
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(""));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(null));

		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(" "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("   "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  \n "));
		
		assertEquals(false, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  f "));
	}

}
