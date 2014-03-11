package nl.tudelft.watchdog.interval.recorded;

import java.io.ObjectInputStream;
import java.util.Date;

import nl.tudelft.watchdog.document.Document;
import nl.tudelft.watchdog.interval.ActivityType;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

/** A recording interval, associated with a {@link Document}. */
public class RecordedInterval implements IInterval {

	private static final long serialVersionUID = 3L;
	private Document document;
	private Date start;
	private Date end;
	private ActivityType activityType;
	private boolean isDebugMode;

	public RecordedInterval(Document document, Date start, Date end,
			ActivityType activityType, boolean debugMode) {
		this.document = document;
		this.start = start;
		this.end = end;
		this.activityType = activityType;
		this.isDebugMode = debugMode;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public Date getStart() {
		return start;
	}

	@Override
	public Date getEnd() {
		return end;
	}

	@Override
	public Duration getDuration() {
		return new Duration(start.getTime(), end.getTime());
	}

	@Override
	public String getDurationString() {
		Duration d = new Duration(start.getTime(), end.getTime());
		Period period = d.toPeriod();
		return PeriodFormat.getDefault().print(period);
	}

	@Override
	public ActivityType getActivityType() {
		return activityType;
	}

	@Override
	public boolean isDebugMode() {
		return isDebugMode;
	}

	// Used to also support deserialization of older versions
	private void readObject(ObjectInputStream ois) {
		// TODO (MMB) this fails in case of a bigger change in the class
		// structure!
		try {
			ois.defaultReadObject();
			setDefaultValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setDefaultValues() {

	}

}