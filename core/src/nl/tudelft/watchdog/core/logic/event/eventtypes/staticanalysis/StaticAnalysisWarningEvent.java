package nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

import java.util.Date;

public class StaticAnalysisWarningEvent extends EventBase {

    private static final long serialVersionUID = -344289578682136085L;

    @SerializedName("sat")
    private final String staticAnalysisType;

    @SerializedName("doc")
    private final Document document;

    public StaticAnalysisWarningEvent(String staticAnalysisType, Document document, TrackingEventType trackingEventType, Date creationDate) {
        super(trackingEventType, creationDate);
        this.staticAnalysisType = staticAnalysisType;
        this.document = document;
    }

    public String getStaticAnalysisType() {
        return this.staticAnalysisType;
    }

    public Document getDocument() {
        return this.document;
    }
}
