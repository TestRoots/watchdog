package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import org.joda.time.DateTime;

import java.util.List;

public class FileWarningSnapshotEvent extends EventBase {

    private static final long serialVersionUID = 1268383797364665026L;

    @SerializedName("doc")
    private final Document document;

    @SerializedName("warnings")
    private final List<Warning<String>> warnings;

    public FileWarningSnapshotEvent(Document document, List<Warning<String>> warnings) {
        super(TrackingEventType.SA_WARNING_SNAPSHOT, DateTime.now().toDate());

        this.document = document;
        this.warnings = warnings;
    }

    public List<Warning<String>> getWarnings() {
        return this.warnings;
    }
}
