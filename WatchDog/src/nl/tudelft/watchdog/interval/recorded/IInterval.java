package nl.tudelft.watchdog.interval.recorded;

import java.io.Serializable;
import java.util.Date;

import nl.tudelft.watchdog.document.IDocument;
import nl.tudelft.watchdog.interval.ActivityType;

import org.joda.time.Duration;

public interface IInterval extends Serializable {

	ActivityType getActivityType();

	IDocument getDocument();

	Date getStart();

	Date getEnd();

	Duration getDuration();

	String getDurationString();

	boolean isDebugMode();

}
