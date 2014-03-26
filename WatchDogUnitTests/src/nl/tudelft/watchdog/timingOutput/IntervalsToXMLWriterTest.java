package nl.tudelft.watchdog.timingOutput;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.ActivityType;
import nl.tudelft.watchdog.logic.interval.IntervalsToXMLWriter;
import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the output of the {@link IntervalsToXMLWriter}.
 */
public class IntervalsToXMLWriterTest {

	/**
	 * A Test for writing one single, simple interval in an XML.
	 */
	@Test
	public void intervalToXMLTest() {
		List<RecordedInterval> intervals = new LinkedList<RecordedInterval>();
		intervals.add(new RecordedInterval(new Document("projname", "filename",
				DocumentType.TEST), new Date(12345678912L), new Date(
				13345355121L), ActivityType.Typing, true));

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new IntervalsToXMLWriter().exportIntervals(intervals, os);

		try {
			String out = new String(os.toByteArray(), "UTF-8");
			Assert.assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><intervals><interval><document><projectName>projname</projectName><fileName>filename</fileName><documentType>TEST</documentType></document><start>12345678912</start><end>13345355121</end><duration>277 hours, 41 minutes, 16 seconds and 209 milliseconds</duration><activityType>Typing</activityType><debugMode>1</debugMode></interval></intervals>",
					out);
		} catch (UnsupportedEncodingException e) {
			Assert.fail();
		}

	}

}
