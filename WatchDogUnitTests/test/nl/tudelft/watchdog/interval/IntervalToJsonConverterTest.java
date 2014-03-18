package nl.tudelft.watchdog.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.document.Document;
import nl.tudelft.watchdog.document.DocumentType;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.interval.recorded.RecordedInterval;

import org.junit.Test;

/**
 * Test the transfer from {@link IInterval}s to JSon.
 */
public class IntervalToJsonConverterTest {

	/**
	 * Tests the format of the returned Json representation.
	 */
	@Test
	public void testJsonRepresentation() {
		ArrayList<IInterval> intervals = new ArrayList<IInterval>();
		intervals.add(new RecordedInterval(new Document("Project",
				"Production.java", DocumentType.PRODUCTION), new Date(1),
				new Date(2), ActivityType.Reading, false));

		IntervalTransferer intervalTransferer = new IntervalTransferer();
		assertEquals(
				"[{\"doc\":{\"pn\":\"Project\",\"fn\":\"Production.java\",\"dt\":\"pr\"},\"ts\":1,\"te\":2,\"at\":\"re\",\"LEGACY_DEBUGMODE\":false}]",
				intervalTransferer.prepareIntervals(intervals));
		System.out.println(intervalTransferer.prepareIntervals(intervals));

	}

}
