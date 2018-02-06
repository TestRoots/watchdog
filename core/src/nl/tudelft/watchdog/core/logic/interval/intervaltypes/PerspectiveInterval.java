package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/** Interval describing a certain opened perspective. */
public class PerspectiveInterval extends IntervalBase {

	/** Constructor. */
	public PerspectiveInterval(Perspective perspectiveType, Date start) {
		super(IntervalType.PERSPECTIVE, start);
		this.perspectiveType = perspectiveType;
	}

	/** Class version. */
	private static final long serialVersionUID = 1L;

	/** The perspective type */
	@SerializedName("pet")
	private Perspective perspectiveType;

	/** The perspective that is open. */
	public enum Perspective {
		@SerializedName("ja")
		JAVA,

		@SerializedName("de")
		DEBUG,

		@SerializedName("ot")
		OTHER
	}

	/** @return the perspective Type. */
	public Perspective getPerspectiveType() {
		return perspectiveType;
	}
}
