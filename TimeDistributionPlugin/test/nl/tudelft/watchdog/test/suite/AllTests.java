package nl.tudelft.watchdog.test.suite;


import nl.tudelft.watchdog.test.interval.ActiveIntervalTest;
import nl.tudelft.watchdog.test.timingOutput.IntervalsToXMLWriterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ActiveIntervalTest.class, IntervalsToXMLWriterTest.class})
public class AllTests {
	
}