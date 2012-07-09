package nl.tudelft.watchdog.interval;

import java.io.Serializable;
import java.util.Date;

import nl.tudelft.watchdog.document.IDocument;

import org.joda.time.Duration;



public interface IInterval extends Serializable {

	IDocument getDocument();

	Date getStart();

	Date getEnd();

	Duration getDuration();

	String getDurationString();

	
}
