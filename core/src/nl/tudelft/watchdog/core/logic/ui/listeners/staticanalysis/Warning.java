package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Warning<T> implements Serializable {

    private static final long serialVersionUID = -573132089990619360L;

    @SerializedName("type")
    public final T type;

    @SerializedName("line")
    public final int lineNumber;

    @SerializedName("time")
    public final Date warningCreationTime;

    @SerializedName("diff")
    public final int secondsBetween;

    public Warning(T type, int lineNumber, Date warningCreationTime) {
        this(type, lineNumber, warningCreationTime, -1);
    }

    public Warning(T type, int lineNumber, Date warningCreationTime, int secondsBetween) {
        this.type = type;
        this.warningCreationTime = warningCreationTime;
        this.secondsBetween = secondsBetween;
        this.lineNumber = lineNumber;
    }
}
