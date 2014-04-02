package nl.tudelft.watchdog.interval;

import nl.tudelft.watchdog.logic.interval.IntervalTransferer;

import org.junit.Test;

public class IntervalTransfererTest {

	@Test
	public void testTransfer() {
		IntervalTransferer it = new IntervalTransferer();
		it.transferData("cca45363247b1a6dc91ecdc4a3a15e1fc85d1c53", "blah");
	}

}
