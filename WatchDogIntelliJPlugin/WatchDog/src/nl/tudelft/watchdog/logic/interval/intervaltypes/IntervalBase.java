package nl.tudelft.watchdog.logic.interval.intervaltypes;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.logic.network.WatchDogTransferable;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import java.io.Serializable;
import java.util.Date;

abstract public class IntervalBase extends WatchDogTransferable implements
        Serializable, Comparable<IntervalBase>, Cloneable{

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
        this.intervalType = type;
        this.start = start;
        this.isClosed = false;
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

    /** Closes this interval. */
    public void close() {
        if (!isClosed()) {
            isClosed = true;
            if (getEnd() == null) {
                setEndTime(new Date());
            }
        }
    }

    /** Sets the start time. */
    public void setStartTime(Date date) {
        this.start = date;
    }

    /** Sets the end time. */
    public void setEndTime(Date date) {
        this.end = date;
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

    /**
     * @return Whether the interval is closed (<code>true</code> in that case),
     *         or not (<code>false</code>).
     */
    public boolean isClosed() {
        return isClosed;
    }

    /** @return the {@link IntervalType}. */
    public IntervalType getType() {
        return intervalType;
    }

    /**
     * Manually sets this interval to closed, without closing it. Users of this
     * class most likely want to invoke {@link #close()} in most cases.
     */
    public void setClosed() {
        this.isClosed = true;
    }

    /** Sets the projectId. */
    public void setSessionSeed(String sessionSeed) {
        this.sessionSeed = sessionSeed;
    }

    @Override
    /** Necessary for storage of Intervals. */
    public int compareTo(IntervalBase comparedInterval) {
        return getEnd().compareTo(comparedInterval.getEnd());
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            return null;
        }
    }
}
