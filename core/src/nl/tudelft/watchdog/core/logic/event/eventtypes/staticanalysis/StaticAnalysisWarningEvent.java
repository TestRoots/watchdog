package nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import org.joda.time.DateTime;

import java.util.Date;

public class StaticAnalysisWarningEvent extends EventBase {

    private static final long serialVersionUID = -344289578682136085L;

    @SerializedName("sat")
    private final String staticAnalysisType;

    @SerializedName("doc")
    private final Document document;

    @SerializedName("time")
    private final Date warningCreationTime;

    @SerializedName("diff")
    private final int warningDifferenceTime;

    @SerializedName("line")
    private final int lineNumber;

    public StaticAnalysisWarningEvent(String staticAnalysisType, Document document, TrackingEventType trackingEventType,
                                      Date eventCreationTime, DateTime warningCreationTime,
                                      int warningDifferenceTime, int lineNumber) {
        super(trackingEventType, eventCreationTime);
        this.staticAnalysisType = staticAnalysisType;
        this.document = document;
        this.warningCreationTime = warningCreationTime.toDate();
        this.warningDifferenceTime = warningDifferenceTime;
        this.lineNumber = lineNumber;
    }

    public String getStaticAnalysisType() {
        return this.staticAnalysisType;
    }

    public Document getDocument() {
        return this.document;
    }

	public int getLineNumber() {
		return this.lineNumber;
	}

	public int getWarningDifferenceTime() {
		return this.warningDifferenceTime;
	}
}
