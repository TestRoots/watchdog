package nl.tudelft.watchdog.core.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import nl.tudelft.watchdog.core.logic.ui.listeners.CoreMarkupModelListener.StaticAnalysisWarning;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class CoreMarkupModelListener {

    private static final Duration CREATION_TRIGGERED_DELAY = Duration.standardSeconds(3);
    private static final long ONE_SECOND = TimeUnit.SECONDS.toMillis(1);

    private final TrackingEventManager trackingEventManager;
    private final Timer timer;
    private final TimerTask timerTask;

    private final Collection<StaticAnalysisWarning> highlightersPerType;

    public CoreMarkupModelListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;

        this.highlightersPerType = new ConcurrentLinkedQueue<>();
        this.timer = new Timer(true);
        this.timerTask = new StaticAnalysisWarningsCollectorTask();

        this.timer.scheduleAtFixedRate(this.timerTask, 0, ONE_SECOND);
    }

    protected void addCreatedWarning(StaticAnalysisType type) {
		highlightersPerType.add(new StaticAnalysisWarning(type, TrackingEventType.SA_WARNING_CREATED));
    }

    protected void addRemovedWarning(StaticAnalysisType type) {
		highlightersPerType.add(new StaticAnalysisWarning(type, TrackingEventType.SA_WARNING_REMOVED));
    }

    public void dispose() {
        this.timerTask.run();
        this.timer.cancel();
    }

    /**
     * Data holder class to store the multiple values required to track an event for.
     */
    protected class StaticAnalysisWarning {
        private final StaticAnalysisType staticAnalysisType;
        private final TrackingEventType trackingEventType;
        DateTime creationTime;

        public StaticAnalysisWarning(StaticAnalysisType staticAnalysisType, TrackingEventType trackingEventType) {
            this.staticAnalysisType = staticAnalysisType;
            this.trackingEventType = trackingEventType;
            creationTime = DateTime.now();
        }
    }

    private class StaticAnalysisWarningsCollectorTask extends TimerTask {
        @Override
        public void run() {
            highlightersPerType.removeIf(warning -> {
                if (warning.creationTime.plus(CREATION_TRIGGERED_DELAY).isBeforeNow()) {
                    trackingEventManager.addEvent(new StaticAnalysisWarningEvent(warning.staticAnalysisType, warning.trackingEventType, warning.creationTime.toDate()));
                    return true;
                }
                return false;
            });
        }
    }
}
