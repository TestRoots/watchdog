package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.network.WatchDogTransferable;

public abstract class EventBase extends WatchDogTransferable implements Serializable {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** The type of the event. */
	@SerializedName("et")
	protected EventType eventType;

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
	public EventBase(EventType type, Date timestamp) {
		this.eventType = type;
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

	/** @return the {@link EventType}. */
	public EventType getType() {
		return eventType;
	}
}
