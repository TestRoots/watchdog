package interval;

import java.util.Date;

import org.joda.time.Duration;

import document.IDocument;


public interface IInterval {

	IDocument getDocument();

	Date getStart();

	Date getEnd();

	Duration getDuration();

	String getDurationString();

	
}
