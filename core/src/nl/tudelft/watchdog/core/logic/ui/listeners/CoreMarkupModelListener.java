package nl.tudelft.watchdog.core.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import org.joda.time.DateTime;

import java.util.stream.Stream;

public class CoreMarkupModelListener {

    private final TrackingEventManager trackingEventManager;
    private Document document;

    public CoreMarkupModelListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
    }

    public CoreMarkupModelListener(Document document, TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
        this.document = document.prepareDocument();
    }

    protected void addCreatedWarnings(Stream<String> types) {
        this.addCreatedWarnings(types, this.document);
    }

    protected void addCreatedWarnings(Stream<String> types, Document document) {
        trackingEventManager.addEvents(types
                .map(type -> new StaticAnalysisWarningEvent(type, document, TrackingEventType.SA_WARNING_CREATED, DateTime.now().toDate()))
        );
    }

    protected void addRemovedWarnings(Stream<String> types) {
        this.addRemovedWarnings(types, this.document);
    }

    protected void addRemovedWarnings(Stream<String> types, Document document) {
        trackingEventManager.addEvents(types
                .map(type -> new StaticAnalysisWarningEvent(type, document, TrackingEventType.SA_WARNING_REMOVED, DateTime.now().toDate()))
        );
    }
}
