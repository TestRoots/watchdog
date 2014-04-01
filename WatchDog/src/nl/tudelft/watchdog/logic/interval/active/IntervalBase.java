package nl.tudelft.watchdog.logic.interval.active;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.google.gson.annotations.SerializedName;

public abstract class IntervalBase implements Serializable {
	protected Timer checkForChangeTimer;
	protected Date timeOfCreation;
	protected boolean isClosed;

	/** Constructor. */
	public IntervalBase() {
		this.timeOfCreation = new Date();
		this.isClosed = false;
	}

	public Date getTimeOfCreation() {
		return timeOfCreation;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public Timer getTimer() {
		return checkForChangeTimer;
	}

	public void closeInterval() {
		isClosed = true;
		checkForChangeTimer.cancel();
		listenForReactivation();
	}

	public abstract void listenForReactivation();

	public abstract ActivityType getActivityType();

	public abstract void addTimeoutListener(long timeout,
			OnInactiveCallBack callbackWhenFinished);

	/** The serialization id. */
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
	private boolean isInDebugMode;

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

	public boolean isDebugMode() {
		return isInDebugMode;
	}

	// Used to also support deserialization of older versions
	private void readObject(ObjectInputStream ois) {
		// TODO (MMB) this fails in case of a bigger change in the class
		// structure!
		try {
			ois.defaultReadObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setEndTime(Date date) {
		this.end = date;
	}

	public void setIsInDebugMode(boolean isInDebugMode) {
		this.isInDebugMode = isInDebugMode;
	}

}
