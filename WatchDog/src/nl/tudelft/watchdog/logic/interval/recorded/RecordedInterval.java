package nl.tudelft.watchdog.logic.interval.recorded;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.interval.ActivityType;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.google.gson.annotations.SerializedName;

/** A recording interval, associated with a {@link Document}. */
public class RecordedInterval implements Serializable {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 3L;

	/** The document associated with this {@link RecordedInterval}. */
	@SerializedName("doc")
	private Document document;

	/** The timestamp start */
	@SerializedName("ts")
	private Date start;

	/** The timestamp end */
	@SerializedName("te")
	private Date end;

	/** The Activity type. */
	@SerializedName("at")
	private ActivityType activityType;

	/** Legacy debug flag. */
	@SerializedName("LEGACY_DEBUGMODE")
	private boolean isDebugMode;

	/** Constructor. */
	public RecordedInterval(Document document, Date start, Date end,
			ActivityType activityType, boolean debugMode) {
		this.document = document;
		this.start = start;
		this.end = end;
		this.activityType = activityType;
		this.isDebugMode = debugMode;
	}

	public Document getDocument() {
		return document;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public Duration getDuration() {
		return new Duration(start.getTime(), end.getTime());
	}

	public String getDurationString() {
		Duration d = new Duration(start.getTime(), end.getTime());
		Period period = d.toPeriod();
		return PeriodFormat.getDefault().print(period);
	}

	public ActivityType getActivityType() {
		return activityType;
	}

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