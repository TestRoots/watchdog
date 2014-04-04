package nl.tudelft.watchdog.logic.interval.active;

import java.util.Date;
import java.util.Timer;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.google.gson.annotations.SerializedName;

/** The interval base. */
public abstract class IntervalBase {

	/** The document associated with this {@link RecordedInterval}. */
	@SerializedName("doc")
	private Document document;

	/** The timestamp start (when this interval was started). */
	@SerializedName("ts")
	private Date start;

	/** The timestamp end (when this interval ended). */
	@SerializedName("te")
	private Date end;

	/** The Activity type. */
	@SerializedName("at")
	protected ActivityType activityType;

	/** Legacy debug flag. */
	@SerializedName("LEGACY_DEBUGMODE")
	private boolean isInDebugMode;

	/** The timer controlling the timeout used for this interval. */
	protected transient Timer checkForChangeTimer;

	/** Whether this interval is closed, or still recording. */
	protected transient boolean isClosed;

	/** Constructor. */
	public IntervalBase(ActivityType activity) {
		this.start = new Date();
		this.isClosed = false;
		this.activityType = activity;
	}

	/**
	 * @return Whether the interval is closed (<code>true</code> in that case),
	 *         or not (<code>false</code>).
	 */
	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * @return The timer.
	 */
	public Timer getTimer() {
		return checkForChangeTimer;
	}

	/** Closes this interval. */
	public void closeInterval() {
		isClosed = true;
		checkForChangeTimer.cancel();
		listenForReactivation();
	}

	/**
	 * @return the document the interval is associated with.
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @return the {@link Date} the interval started.
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @return the end of the interval as a {@link Date}.
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * @return the duration.
	 */
	public Duration getDuration() {
		return new Duration(start.getTime(), end.getTime());
	}

	/**
	 * @return A human-readable duration.
	 */
	public String getDurationString() {
		Duration duration = new Duration(start.getTime(), end.getTime());
		Period period = duration.toPeriod();
		return PeriodFormat.getDefault().print(period);
	}

	/**
	 * @return <code>true</code> if in DebugMode, <code>false</code> otherwise.
	 */
	public boolean isDebugMode() {
		return isInDebugMode;
	}

	/** Sets the debug mode. */
	public void setIsInDebugMode(boolean isInDebugMode) {
		this.isInDebugMode = isInDebugMode;
	}

	/** Sets the document. */
	public void setDocument(Document document) {
		this.document = document;
	}

	/** Sets the start time. */
	public void setStartTime(Date date) {
		this.start = date;
	}

	/** Sets the end time. */
	public void setEndTime(Date date) {
		this.end = date;
	}

	/** @return the {@link ActivityType}. */
	public ActivityType getActivityType() {
		return activityType;
	}

	/** Listener for reactivation of this interval. */
	// TODO (MMB) once redesign of classes is complete, not sure if we still
	// need this
	public abstract void listenForReactivation();

	/** Adds a timeout listener. */
	public abstract void addTimeoutListener(long timeout,
			OnInactiveCallback callbackWhenFinished);

}
