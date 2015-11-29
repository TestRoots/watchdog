package nl.tudelft.watchdog.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Tests for the {@link WatchDogUtils}.
 */
public class WatchDogUtilsTest {

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
	public void testIsEmptySansWhitespace() {
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(""));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(null));

		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces(" "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("   "));
		assertEquals(true, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  \n "));

		assertEquals(false, WatchDogUtils.isEmptyOrHasOnlyWhitespaces("  f "));
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
	public void testHashFileNameEmpty() {
		String expectedHash = "";
		assertEquals(expectedHash, WatchDogUtils.createFileNameHash(""));
		assertEquals(expectedHash, WatchDogUtils.createFileNameHash(null));
	}

	@Test
	public void testFileExtensionReplace() {
		String fileName = "ATestClass";
		String expected = WatchDogUtils.createFileNameHash(fileName + ".java");
		assertEquals(expected, WatchDogUtils.createFileNameHash(fileName));
	}

	@Test
	public void testMavenTestFileNotation() {
		String fileName = "ATestClass";
		String expected = WatchDogUtils.createFileNameHash("org.some.package."
				+ fileName);
		assertEquals(expected, WatchDogUtils.createFileNameHash(fileName));
	}

	@Test
	public void testBugReplaceWithinStringDoesNotOccur() {
		String unexpectedHash = WatchDogUtils
				.createFileNameHash("AClassTest.java");
		assertTrue(!WatchDogUtils.createFileNameHash("ATestClassTest.java")
				.equals(unexpectedHash));
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
