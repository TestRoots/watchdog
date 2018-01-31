package nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

import java.util.Date;

public class StaticAnalysisWarningEvent extends EventBase {

    @SerializedName("sat")
    private final StaticAnalysisType staticAnalysisType;

    public StaticAnalysisWarningEvent(StaticAnalysisType staticAnalysisType, TrackingEventType trackingEventType, Date creationDate) {
        super(trackingEventType, creationDate);
        this.staticAnalysisType = staticAnalysisType;
    }
}
