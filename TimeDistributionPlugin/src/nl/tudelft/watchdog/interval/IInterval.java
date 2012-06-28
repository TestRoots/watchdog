package nl.tudelft.watchdog.interval;

import java.util.Date;

import nl.tudelft.watchdog.document.IDocument;

import org.joda.time.Duration;



public interface IInterval {

	IDocument getDocument();

	Date getStart();

	Date getEnd();

	Duration getDuration();

	String getDurationString();

	
}
