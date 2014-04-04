package nl.tudelft.watchdog.interval;

import nl.tudelft.watchdog.logic.interval.IntervalTransferer;

import org.junit.Test;

public class IntervalTransfererTest {

	@Test
	public void testTransfer() {
		IntervalTransferer it = new IntervalTransferer();
		it.transferData("blubb", "blah");
	}

}
