package nl.tudelft.watchdog.test.suite;


import nl.tudelft.watchdog.test.document.DocumentClassifierTest;
import nl.tudelft.watchdog.test.document.DocumentFactoryTest;
import nl.tudelft.watchdog.test.interval.ActiveIntervalTest;
import nl.tudelft.watchdog.test.timingOutput.IntervalsToXMLWriterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ActiveIntervalTest.class, IntervalsToXMLWriterTest.class, DocumentClassifierTest.class, DocumentFactoryTest.class})
public class AllTests {
	
}