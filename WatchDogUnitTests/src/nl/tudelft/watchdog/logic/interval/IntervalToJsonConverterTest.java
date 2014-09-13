package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.logic.network.JsonTransferer;

import org.junit.Test;

/**
 * Test the transfer from {@link IInterval}s to JSon.
 */
public class IntervalToJsonConverterTest {

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonReadingIntervalRepresentation() {
		ReadingInterval interval = new ReadingInterval(null);
		interval.setUserid("123");
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		JsonTransferer intervalTransferer = new JsonTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"re\",\"ts\":1,\"te\":2,\"ss\":0,\"uid\":\"123\",\"wdv\":\"1.0-SNAPSHOT\"}]",
				intervalTransferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonTypingIntervalRepresentation() {
		TypingInterval interval = new TypingInterval(null);
		interval.setUserid("123");
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		JsonTransferer intervalTransferer = new JsonTransferer();
		assertEquals(
				"[{\"diff\":0,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"ty\",\"ts\":1,\"te\":2,\"ss\":0,\"uid\":\"123\",\"wdv\":\"1.0-SNAPSHOT\"}]",
				intervalTransferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonSessionIntervalRepresentation() {
		IntervalBase interval = new IntervalBase(IntervalType.ECLIPSE_OPEN);
		interval.setUserid("123");
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		JsonTransferer intervalTransferer = new JsonTransferer();
		assertEquals(
				"[{\"it\":\"eo\",\"ts\":1,\"te\":2,\"ss\":0,\"uid\":\"123\",\"wdv\":\"1.0-SNAPSHOT\"}]",
				intervalTransferer.toJson(intervals));
	}

	private ArrayList<IntervalBase> createSampleIntervals(
			EditorIntervalBase interval) {
		ArrayList<IntervalBase> intervals = new ArrayList<IntervalBase>();
		interval.setDocument(new Document("Project", "Production.java", "blah-document"));
		interval.close();
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		intervals.add(interval);
		return intervals;
	}

	private ArrayList<IntervalBase> createSampleIntervals(IntervalBase interval) {
		ArrayList<IntervalBase> intervals = new ArrayList<IntervalBase>();
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		intervals.add(interval);
		return intervals;
	}
}
