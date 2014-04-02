package nl.tudelft.watchdog.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.IntervalTransferer;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;
import nl.tudelft.watchdog.logic.interval.active.TypingInterval;

import org.junit.Test;

/**
 * Test the transfer from {@link IInterval}s to JSon.
 */
public class IntervalToJsonConverterTest {

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonReadingIntervalRepresentation() {
		ReadingInterval interval = new ReadingInterval(null);
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		IntervalTransferer intervalTransferer = new IntervalTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"Project\",\"fn\":\"Production.java\",\"dt\":\"pr\"},\"ts\":1,\"te\":2,\"at\":\"re\",\"LEGACY_DEBUGMODE\":false}]",
				intervalTransferer.prepareIntervals(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonTypingIntervalRepresentation() {
		TypingInterval interval = new TypingInterval(null);
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		IntervalTransferer intervalTransferer = new IntervalTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"Project\",\"fn\":\"Production.java\",\"dt\":\"pr\"},\"ts\":1,\"te\":2,\"at\":\"ty\",\"LEGACY_DEBUGMODE\":false}]",
				intervalTransferer.prepareIntervals(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonSessionIntervalRepresentation() {

		SessionInterval interval = new SessionInterval();
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		IntervalTransferer intervalTransferer = new IntervalTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"Project\",\"fn\":\"Production.java\",\"dt\":\"pr\"},\"ts\":1,\"te\":2,\"at\":\"se\",\"LEGACY_DEBUGMODE\":false}]",
				intervalTransferer.prepareIntervals(intervals));
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
