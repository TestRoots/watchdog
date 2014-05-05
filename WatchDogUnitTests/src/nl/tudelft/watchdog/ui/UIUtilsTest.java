package nl.tudelft.watchdog.ui;

import static org.junit.Assert.*;

import org.junit.Test;

public class UIUtilsTest {

	@Test
	public void testIsEmpty() {
		assertEquals(true, UIUtils.isEmpty(""));
		assertEquals(true, UIUtils.isEmpty(null));

		assertEquals(false, UIUtils.isEmpty(" "));
		assertEquals(false, UIUtils.isEmpty("ffanynormalstring"));
	}

	@Test
	public void testIsEmptySansWhitespace() {
		assertEquals(true, UIUtils.isEmptyOrWhitespaces(""));
		assertEquals(true, UIUtils.isEmptyOrWhitespaces(null));

		assertEquals(true, UIUtils.isEmptyOrWhitespaces(" "));
		assertEquals(true, UIUtils.isEmptyOrWhitespaces("  "));
		assertEquals(true, UIUtils.isEmptyOrWhitespaces("   "));
		assertEquals(true, UIUtils.isEmptyOrWhitespaces("  \n "));
		
		assertEquals(false, UIUtils.isEmptyOrWhitespaces("  f "));
	}

}
