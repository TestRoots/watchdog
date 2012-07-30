package nl.tudelft.watchdog.test.timingOutput;


import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nl.tudelft.watchdog.document.Document;
import nl.tudelft.watchdog.document.DocumentType;
import nl.tudelft.watchdog.interval.ActivityType;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.junit.Assert;
import org.junit.Test;


public class IntervalsToXMLWriterTest {
	
	@Test
	public void intervalToXMLTest() {
		List<IInterval> intervals = new LinkedList<IInterval>();
		intervals.add(new RecordedInterval(new Document("projname","filename", DocumentType.TEST), new Date(12345678912L), new Date(13345355121L), ActivityType.Editing));
		
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		new IntervalsToXMLWriter().exportIntervals(intervals, os);
		
		try {
			String out = new String(os.toByteArray(), "UTF-8");
			Assert.assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><intervals><interval><document><projectName>projname</projectName><fileName>filename</fileName><documentType>TEST</documentType></document><start>12345678912</start><end>13345355121</end><duration>277 hours, 41 minutes, 16 seconds and 209 milliseconds</duration><activityType>Editing</activityType></interval></intervals>"	
					, out);
		} catch (UnsupportedEncodingException e) {
			Assert.fail();
		}
		
	}
	
}
