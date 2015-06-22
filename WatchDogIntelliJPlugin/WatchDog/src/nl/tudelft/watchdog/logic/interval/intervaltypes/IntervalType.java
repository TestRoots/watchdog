package nl.tudelft.watchdog.logic.interval.intervaltypes;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration depicting the different possible activities a developer can
 * perform.
 */
public enum IntervalType {
    /** Users types in the IDE. */
    @SerializedName("ty")
    TYPING,

    /** User reads, ie. no key strokes detectable. */
    @SerializedName("re")
    READING,

    /** IntelliJOpen. */
    @SerializedName("eo")
    INTELLIJ_OPEN,

    /** IntelliJActive. */
    @SerializedName("ea")
    INTELLIJ_ACTIVE,

    /** A Junit execution interval. */
    @SerializedName("ju")
    JUNIT,

    /** The user is active */
    @SerializedName("ua")
    USER_ACTIVE,

    /** The WatchDogView is open. */
    @SerializedName("wo")
    WATCHDOGVIEW
}
