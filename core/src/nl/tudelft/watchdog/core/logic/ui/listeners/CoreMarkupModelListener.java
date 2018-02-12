package nl.tudelft.watchdog.core.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import org.joda.time.DateTime;

import java.util.stream.Stream;

public class CoreMarkupModelListener {

    private final TrackingEventManager trackingEventManager;

    public CoreMarkupModelListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
    }

    protected void addCreatedWarnings(Stream<StaticAnalysisType> types) {
		trackingEventManager.addEvents(types
                .map(type -> new StaticAnalysisWarningEvent(type, TrackingEventType.SA_WARNING_CREATED, DateTime.now().toDate()))
        );
    }

    protected void addRemovedWarnings(Stream<StaticAnalysisType> types) {
        trackingEventManager.addEvents(types
                .map(type -> new StaticAnalysisWarningEvent(type, TrackingEventType.SA_WARNING_REMOVED, DateTime.now().toDate()))
        );
    }
}
