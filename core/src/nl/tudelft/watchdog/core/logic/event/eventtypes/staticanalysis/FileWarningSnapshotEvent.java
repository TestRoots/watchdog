package nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.Warning;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Analog to a {@link StaticAnalysisWarningEvent}, but this class it stores a list of {@link Warning},
 * as a snapshot of the current state of the {@link #document}. If you need to save multiple events,
 * use this event rather than {@link StaticAnalysisWarningEvent}.
 */
public class FileWarningSnapshotEvent extends EventBase {

    private static final long serialVersionUID = 1268383797364665026L;

    @SerializedName("doc")
    public final Document document;

    @SerializedName("warnings")
    public final List<Warning<String>> warnings;

    public FileWarningSnapshotEvent(Document document, List<Warning<String>> warnings) {
        super(TrackingEventType.SA_WARNING_SNAPSHOT, DateTime.now().toDate());

        this.document = document;
        this.warnings = warnings;
    }
}
