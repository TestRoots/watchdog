package nl.tudelft.watchdog.logic.network;

import static org.junit.Assert.*;
import nl.tudelft.watchdog.eclipse.logic.network.ClientVersionChecker;

import org.junit.Test;

public class ClientVersionCheckerTest {

	@Test
	public void test() {
		String version1 = "1.0.0";
		assertFalse(ClientVersionChecker.hasMajorOrMinorVersionGap(version1,
				version1));
		assertFalse(ClientVersionChecker.hasMajorOrMinorVersionGap(version1,
				"1.0.0-SNAPSHOT"));
		assertFalse(ClientVersionChecker.hasMajorOrMinorVersionGap(version1,
				"1.0.1"));
		assertTrue(ClientVersionChecker.hasMajorOrMinorVersionGap(version1,
				"1.1.1.SNAPSHOT"));
		assertTrue(ClientVersionChecker.hasMajorOrMinorVersionGap(version1,
				"1.1.1"));
		assertTrue(ClientVersionChecker.hasMajorOrMinorVersionGap(version1,
				"1.2"));
		assertTrue(ClientVersionChecker.hasMajorOrMinorVersionGap(version1,
				"2.0.0"));
	}
}
