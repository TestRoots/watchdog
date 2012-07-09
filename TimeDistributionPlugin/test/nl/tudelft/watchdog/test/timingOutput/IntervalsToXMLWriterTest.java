package nl.tudelft.watchdog.test.timingOutput;


import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nl.tudelft.watchdog.document.Document;
import nl.tudelft.watchdog.document.DocumentType;
import nl.tudelft.watchdog.interval.IInterval;
import nl.tudelft.watchdog.interval.RecordedInterval;
import nl.tudelft.watchdog.timingOutput.IntervalsToXMLWriter;

import org.junit.Assert;
import org.junit.Test;


public class IntervalsToXMLWriterTest {
	
	@Test
	public void intervalToXMLTest() {
		List<IInterval> intervals = new LinkedList<IInterval>();
		intervals.add(new RecordedInterval(new Document("filename", DocumentType.TEST), new Date(12345678912L), new Date(13345355121L)));
		
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		new IntervalsToXMLWriter().exportIntervals(intervals, os);
		
		try {
			String out = new String(os.toByteArray(), "UTF-8");
			Assert.assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><Intervals><Interval><Document><fileName>filename</fileName><documentType>TEST</documentType></Document><Start>12345678912</Start><End>13345355121</End><duration>277 hours, 41 minutes, 16 seconds and 209 milliseconds</duration></Interval></Intervals>"	
					, out);
		} catch (UnsupportedEncodingException e) {
			Assert.fail();
		}
		
	}
	
}
