package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/** The interval base. */
abstract public class IntervalBase extends WatchDogItem implements Serializable, Comparable<WatchDogItem>, Cloneable {

	/** The version id of this class. */
	private static final long serialVersionUID = 2L;

	/** The Activity type. */
	@SerializedName("it")
	protected IntervalType intervalType;

	/** The timestamp start (when this interval was started). */
	@SerializedName("ts")
	private Date start;

	/** The timestamp end (when this interval ended). */
	@SerializedName("te")
	private Date end;

	/**
	 * The session seed, a random string generated on each start of Eclipse to
	 * be able to tell running Eclipse instances apart.
	 */
	@SerializedName("ss")
	protected String sessionSeed;

	/** Whether this interval is closed, or still recording. */
	protected transient boolean isClosed;

	/** Constructor. */
	public IntervalBase(IntervalType type, Date start) {
		this.start = start;
		this.isClosed = false;
		this.intervalType = type;
	}

	/**
	 * @return Whether the interval is closed (<code>true</code> in that case),
	 *         or not (<code>false</code>).
	 */
	public boolean isClosed() {
		return isClosed;
	}

	/** Closes this interval. */
	public void close() {
		if (!isClosed()) {
			isClosed = true;
			if (getEnd() == null) {
				setEndTime(new Date());
			}
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
		if (end != null) {
			return end;
		}
		return new Date();
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

	/** @return A human-readable duration. */
	public String getDurationString() {
		Duration duration = getDuration();
		Period period = duration.toPeriod();
		return PeriodFormat.getDefault().print(period);
	}

	/** Sets the start time. */
	public void setStartTime(Date date) {
		this.start = date;
	}

	/** Sets the end time. */
	public void setEndTime(Date date) {
		this.end = date;
	}

	/** Sets the projectId. */
	public void setSessionSeed(String sessionSeed) {
		this.sessionSeed = sessionSeed;
	}

	/** @return the {@link IntervalType}. */
	public IntervalType getType() {
		return intervalType;
	}

	/**
	 * Necessary for storage of Intervals. The comparison is first based on the
	 * end dates of the two intervals. If these dates are equal but the
	 * intervals themselves not, the comparison is based on the start dates. If
	 * the start dates are also equal, the type of the intervals is used to
	 * produce the result of this method. This sequence of step is required to
	 * ensure that intervals are not lost when two or more intervals have the
	 * same end date.
	 *
	 * If the item to compare to isn't an instance of IntervalBase, the class
	 * name is used to determine the result of the comparison.
	 */
	public int compareTo(WatchDogItem comparedItem) {
		if (comparedItem instanceof IntervalBase) {
			IntervalBase comparedInterval = (IntervalBase) comparedItem;
			int res = getEnd().compareTo(comparedInterval.getEnd());
			if (res == 0 && !this.equals(comparedInterval)) {
				res = getStart().compareTo(comparedInterval.getStart());
				if (res == 0) {
					res = getType().compareTo(comparedInterval.getType()) > 0 ? 1 : -1;
				}
			}
			return res;
		}
		return this.getClass().getName().compareTo(comparedItem.getClass().getName());
	}

	/**
	 * Manually sets this interval to closed, without closing it. Users of this
	 * class most likely want to invoke {@link #close()} in most cases.
	 */
	public void setClosed() {
		this.isClosed = true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Checks whether the parameter is an IntervalBase and is equal to this by
	 * comparing the start dates, end dates, types and session seeds of the two
	 * intervals.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IntervalBase other = (IntervalBase) obj;
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (intervalType != other.intervalType) {
			return false;
		}
		if (sessionSeed == null) {
			if (other.sessionSeed != null) {
				return false;
			}
		} else if (!sessionSeed.equals(other.sessionSeed)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}

}
