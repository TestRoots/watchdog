package nl.tudelft.watchdog.core.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import org.joda.time.DateTime;

import java.util.stream.Stream;

public class CoreMarkupModelListener {

    private final TrackingEventManager trackingEventManager;
    private final Document document;

    public CoreMarkupModelListener(Document document, TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
        this.document = document.prepareDocument();
    }

    protected void addCreatedWarnings(Stream<String> types) {
		trackingEventManager.addEvents(types
                .map(type -> new StaticAnalysisWarningEvent(type, this.document, TrackingEventType.SA_WARNING_CREATED, DateTime.now().toDate()))
        );
    }

    protected void addRemovedWarnings(Stream<String> types) {
        trackingEventManager.addEvents(types
                .map(type -> new StaticAnalysisWarningEvent(type, this.document, TrackingEventType.SA_WARNING_REMOVED, DateTime.now().toDate()))
        );
    }
}
