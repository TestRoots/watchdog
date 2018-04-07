package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

public abstract class EventBase extends WatchDogItem implements Serializable, Comparable<WatchDogItem> {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** The type of the event. */
	@SerializedName("et")
	protected TrackingEventType trackingEventType;

	/** The timestamp of the event. */
	@SerializedName("ts")
	private Date timestamp;

	/**
	 * The session seed, a random string generated on each start of Eclipse to
	 * be able to tell running Eclipse instances apart.
	 */
	@SerializedName("ss")
	protected String sessionSeed;

	/** Constructor. */
	public EventBase(TrackingEventType type, Date timestamp) {
		this.trackingEventType = type;
		this.timestamp = timestamp;
	}

	/**
	 * @return the {@link Date} the event occurred
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/** Sets the timestamp of this event. */
	public void setTimestamp(Date ts) {
		this.timestamp = ts;
	}

	/** Sets the projectId. */
	public void setSessionSeed(String sessionSeed) {
		this.sessionSeed = sessionSeed;
	}

	/** @return the {@link TrackingEventType}. */
	public TrackingEventType getType() {
		return trackingEventType;
	}

	/**
	 * Necessary for the storage of events and intervals. The comparison is
	 * first based on the timestamps of the two events. If these dates are equal
	 * but the events themselves are not, the type of the events is used to
	 * produce the result of this method. These two steps are required to ensure
	 * that events are not lost when two or more events have the same timestamp.
	 *
	 * If the item to compare to isn't an instance of EventBase, the class name
	 * is used to determine the result of the comparison.
	 */
	public int compareTo(WatchDogItem comparedItem) {
		if (comparedItem instanceof EventBase) {
			EventBase comparedEvent = (EventBase) comparedItem;
			int res = getTimestamp().compareTo(comparedEvent.getTimestamp());
			if (res == 0 && !this.equals(comparedEvent)) {
				res = getType().compareTo(comparedEvent.getType()) > 0 ? 1 : -1;
			}
			return res;
		}
		return this.getClass().getName().compareTo(comparedItem.getClass().getName());
	}

	/**
	 * Checks whether the parameter is an EventBase and is equal to this by
	 * comparing the timestamps, types and session seeds of the two events.
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
		EventBase other = (EventBase) obj;
		if (trackingEventType != other.trackingEventType) {
			return false;
		}
		if (sessionSeed == null) {
			if (other.sessionSeed != null) {
				return false;
			}
		} else if (!sessionSeed.equals(other.sessionSeed)) {
			return false;
		}
		if (timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!timestamp.equals(other.timestamp)) {
			return false;
		}
		return true;
	}
}
