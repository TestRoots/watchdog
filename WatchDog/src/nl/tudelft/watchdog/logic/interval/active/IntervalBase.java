package nl.tudelft.watchdog.logic.interval.active;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

import nl.tudelft.watchdog.util.WatchDogUtils;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/** The interval base. */
public abstract class IntervalBase implements Serializable {

	/** The version id of this class. */
	private static final long serialVersionUID = 2L;

	/** The timestamp start (when this interval was started). */
	@SerializedName("ts")
	private Date start;

	/** The timestamp end (when this interval ended). */
	@SerializedName("te")
	private Date end;

	/** The Activity type. */
	@SerializedName("it")
	protected IntervalType intervalType;

	/**
	 * The session seed, a random number generated on each start of Eclipse to
	 * be able to tell running Eclipse instances apart.
	 */
	@SerializedName("ss")
	protected long sessionSeed;

	/** Legacy debug flag. */
	@SerializedName("LEGACY_DEBUGMODE")
	private boolean isInDebugMode;

	/** The projectId this interval is transfered from */
	@SerializedName("pid")
	private String projectId;

	/** The userId this interval is transfered from */
	@SerializedName("uid")
	private String userId;

	/** Whether this interval is closed, or still recording. */
	protected transient boolean isClosed;

	/** Constructor. */
	public IntervalBase(IntervalType activity, long sessionSeed) {
		this.start = new Date();
		this.isClosed = false;
		this.intervalType = activity;
		this.sessionSeed = sessionSeed;
	}

	/**
	 * @return Whether the interval is closed (<code>true</code> in that case),
	 *         or not (<code>false</code>).
	 */
	public boolean isClosed() {
		return isClosed;
	}

	/** The timer controlling the timeout used for this interval. */
	protected transient Timer checkForChangeTimer;

	/**
	 * @return The timer.
	 */
	public Timer getTimer() {
		return checkForChangeTimer;
	}

	/** Closes this interval. */
	public void closeInterval() {
		if (!isClosed()) {
			isClosed = true;
			if (checkForChangeTimer != null) {
				checkForChangeTimer.cancel();
			}
			setEndTime(new Date());
			setIsInDebugMode(WatchDogUtils.isInDebugMode());
		}
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
	 * @return the duration of this interval. If the interval is not yet closed,
	 *         return the duration until now.
	 */
	public Duration getDuration() {
		if (isClosed) {
			return new Duration(start.getTime(), end.getTime());
		}
		return new Duration(start.getTime(), new Date().getTime());
	}

	/**
	 * @return A human-readable duration.
	 */
	public String getDurationString() {
		Duration duration = getDuration();
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

	/** Sets the start time. */
	public void setStartTime(Date date) {
		this.start = date;
	}

	/** Sets the end time. */
	public void setEndTime(Date date) {
		this.end = date;
	}

	/** Sets the userId. */
	public void setUserid(String userId) {
		this.userId = userId;
	}

	/** Sets the projectId. */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/** @return the {@link ActivityType}. */
	public IntervalType getActivityType() {
		return intervalType;
	}

	/** Convert this to a JSON string */
	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
