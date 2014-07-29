package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;
import nl.tudelft.watchdog.logic.interval.active.TypingInterval;
import nl.tudelft.watchdog.logic.network.JsonTransferer;

import org.junit.Test;

/**
 * Test the transfer from {@link IInterval}s to JSon.
 */
public class IntervalToJsonConverterTest {

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonReadingIntervalRepresentation() {
		ReadingInterval interval = new ReadingInterval(null, "123", 0);
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		JsonTransferer intervalTransferer = new JsonTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"Project\",\"fn\":\"Production.java\",\"dt\":\"pr\"},\"ts\":1,\"te\":2,\"it\":\"re\",\"ss\":0,\"LEGACY_DEBUGMODE\":false,\"userid\":\"123\"}]",
				intervalTransferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonTypingIntervalRepresentation() {
		TypingInterval interval = new TypingInterval(null, "123", 0);
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		JsonTransferer intervalTransferer = new JsonTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"Project\",\"fn\":\"Production.java\",\"dt\":\"pr\"},\"ts\":1,\"te\":2,\"it\":\"ty\",\"ss\":0,\"LEGACY_DEBUGMODE\":false,\"userid\":\"123\"}]",
				intervalTransferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonSessionIntervalRepresentation() {

		SessionInterval interval = new SessionInterval("123", 0);
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		JsonTransferer intervalTransferer = new JsonTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"Project\",\"fn\":\"Production.java\",\"dt\":\"pr\"},\"ts\":1,\"te\":2,\"it\":\"se\",\"ss\":0,\"LEGACY_DEBUGMODE\":false,\"userid\":\"123\"}]",
				intervalTransferer.toJson(intervals));
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
