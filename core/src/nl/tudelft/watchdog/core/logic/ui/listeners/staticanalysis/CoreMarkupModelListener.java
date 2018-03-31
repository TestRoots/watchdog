package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import org.joda.time.DateTime;

import java.util.stream.Stream;

/**
 * Base class for a MarkupModelListener intended to listen to static analysis warning changes.
 * It can processes {@link Stream}s of {@link Warning}s and generates the corresponding
 * {@link StaticAnalysisWarningEvent} which is added to the {@link #trackingEventManager}.
 */
public class CoreMarkupModelListener {

    @SuppressWarnings("WeakerAccess")
	public static void addCreatedWarnings(TrackingEventManager trackingEventManager, Stream<Warning<String>> createdWarnings, Document document) {
        // Prepare the document again to update the line numbers and other statistics
        document.prepareDocument();
        trackingEventManager.addEvents(createdWarnings
                .map(warning -> createEventFromWarning(TrackingEventType.SA_WARNING_CREATED, warning, document))
        );
    }

    @SuppressWarnings("WeakerAccess")
    public static void addRemovedWarnings(TrackingEventManager trackingEventManager, Stream<Warning<String>> removedWarnings, Document document) {
        // Prepare the document again to update the line numbers and other statistics
        document.prepareDocument();
        trackingEventManager.addEvents(removedWarnings.map(warning ->
                createEventFromWarning(TrackingEventType.SA_WARNING_REMOVED, warning, document))
        );
    }

    private static StaticAnalysisWarningEvent createEventFromWarning(TrackingEventType trackingEventType, Warning<String> warning, Document document) {
        return new StaticAnalysisWarningEvent(
                warning,
                document,
                trackingEventType,
                DateTime.now().toDate()
        );
    }

}
