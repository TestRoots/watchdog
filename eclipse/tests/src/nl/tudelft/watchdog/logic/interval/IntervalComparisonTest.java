package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IDEOpenInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.UserActiveInterval;

/**
 * Test class for comparing intervals by testing the IntervalBase.compareTo
 * method.
 */
public class IntervalComparisonTest {

	@Test
	public void testComparisonEqualIntervalsSameObject() {
		UserActiveInterval interval = new UserActiveInterval(new Date());
		interval.close();
		assertEquals(0, interval.compareTo(interval));
	}

	@Test
	public void testComparisonEqualIntervalsDifferentObjectsSameType() {
		Date start = new Date();
		UserActiveInterval interval1 = new UserActiveInterval(start);
		UserActiveInterval interval2 = new UserActiveInterval(start);
		Date end = new Date();
		interval1.close();
		interval1.setEndTime(end);
		interval2.close();
		interval2.setEndTime(end);
		assertEquals(0, interval1.compareTo(interval2));
	}

	@Test
	public void testComparisonEqualIntervalsDifferentObjectsDifferentType() {
		Date start = new Date();
		UserActiveInterval interval1 = new UserActiveInterval(start);
		IDEOpenInterval interval2 = new IDEOpenInterval(start);
		interval1.close();
		interval2.close();
		interval2.setEndTime(interval1.getEnd());
		assertEquals(1, interval1.compareTo(interval2));
		assertEquals(-1, interval2.compareTo(interval1));
	}

	@Test
	public void testComparisonEqualIntervalsDifferentObjectsDifferentStart() {
		UserActiveInterval interval1 = new UserActiveInterval(new Date(1));
		UserActiveInterval interval2 = new UserActiveInterval(new Date(2));
		interval1.close();
		interval2.close();
		interval2.setEndTime(interval1.getEnd());
		assertEquals(-1, interval1.compareTo(interval2));
		assertEquals(1, interval2.compareTo(interval1));
	}

	@Test
	public void testComparisonTwoDifferentIntervalsDifferentType() {
		UserActiveInterval interval1 = new UserActiveInterval(new Date(1));
		IDEOpenInterval interval2 = new IDEOpenInterval(new Date(2));
		interval1.close();
		interval1.setEndTime(new Date(2));
		interval2.close();
		interval2.setEndTime(new Date(3));
		assertEquals(-1, interval1.compareTo(interval2));
		assertEquals(1, interval2.compareTo(interval1));
	}

	@Test
	public void testComparisonTwoDifferentIntervalsSameType() {
		UserActiveInterval interval1 = new UserActiveInterval(new Date(1));
		UserActiveInterval interval2 = new UserActiveInterval(new Date(2));
		interval1.close();
		interval1.setEndTime(new Date(2));
		interval2.close();
		interval2.setEndTime(new Date(3));
		assertEquals(-1, interval1.compareTo(interval2));
		assertEquals(1, interval2.compareTo(interval1));
	}
}
