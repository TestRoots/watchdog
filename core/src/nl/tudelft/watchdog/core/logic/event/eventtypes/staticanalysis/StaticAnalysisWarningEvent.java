package nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.Warning;

import java.util.Date;

/**
 * Event that stores a specific {@link Warning} for a specific {@link Document}.
 */
public class StaticAnalysisWarningEvent extends EventBase {

    private static final long serialVersionUID = -344289578682136085L;

    @SerializedName("doc")
    public final Document document;

    @SerializedName("warning")
    public final Warning<String> warning;

    public StaticAnalysisWarningEvent(Warning<String> warning, Document document,
                                      TrackingEventType trackingEventType, Date eventCreationTime) {
        super(trackingEventType, eventCreationTime);
        this.warning = warning;
        this.document = document;
    }
}
