package nl.tudelft.watchdog.interval;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.IntervalTransferer;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;

import org.junit.Test;

public class IntervalTransfererTest {

	@Test
	public void testTransfer() {
		IntervalTransferer it = new IntervalTransferer();
		SessionInterval interval = new SessionInterval();
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);
		String json = it.toJson(intervals);
		
		it.transferJson("cca45363247b1a6dc91ecdc4a3a15e1fc85d1c53", json);
	}
	
	private ArrayList<IntervalBase> createSampleIntervals(IntervalBase interval) {
		ArrayList<IntervalBase> intervals = new ArrayList<IntervalBase>();
		interval.setDocument(new Document("Project", "Production.java",
				DocumentType.PRODUCTION));
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		interval.setIsInDebugMode(false);
		intervals.add(interval);
		return intervals;
	}

}
