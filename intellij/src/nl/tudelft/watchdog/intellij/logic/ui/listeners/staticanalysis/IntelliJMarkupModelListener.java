package nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.impl.MarkupModelImpl;
import com.intellij.openapi.editor.impl.event.MarkupModelListener;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class IntelliJMarkupModelListener implements MarkupModelListener, Disposable {

    private static final Duration CREATION_TRIGGERED_DELAY = Duration.standardSeconds(3);
    private static final long ONE_SECOND = TimeUnit.SECONDS.toMillis(1);

    private final TrackingEventManager trackingEventManager;
    private final Collection<StaticAnalysisWarning> highlightersPerType;
    private final TimerTask timerTask;
    private final Timer timer;

    private IntelliJMarkupModelListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;

        this.highlightersPerType = new ConcurrentLinkedQueue<>();
        this.timer = new Timer(true);
        this.timerTask = new StaticAnalysisWarningsCollectorTask();

        this.timer.scheduleAtFixedRate(this.timerTask, 0, ONE_SECOND);
    }

    public static IntelliJMarkupModelListener initializeAfterAnalysisFinished(
            Project project, Disposable disposable, Document document, TrackingEventManager trackingEventManager) {

        final IntelliJMarkupModelListener markupModelListener = new IntelliJMarkupModelListener(trackingEventManager);

        // We need to run this in smart mode, because the very first time you start your editor, it is very briefly
        // in dumb mode and the codeAnalyzer thinks (incorrectly) it is  finished.
        // Therefore, wait for smart mode and only then start listening, to make sure the codeAnalyzer actually did its thing.
        DumbServiceImpl.getInstance(project).runWhenSmart(() -> {
            final DaemonCodeAnalyzerImpl analyzer = (DaemonCodeAnalyzerImpl) DaemonCodeAnalyzer.getInstance(project);
            final MessageBusConnection messageBusConnection = project.getMessageBus().connect(disposable);

            messageBusConnection.subscribe(DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC, new DaemonCodeAnalyzer.DaemonListenerAdapter() {
                @Override
                public void daemonFinished() {
                    // We only receive global Code Analyzer events. This means that once such an event triggered,
                    // we do not know for which file the analysis actually succeeded. Therefore we need to check
                    // the so-called "dirty" state of the file for this particular analyzer to check whether
                    // the analyzer actually finished. In this case, `null` indicates: the file is not dirty.
                    if (analyzer.getFileStatusMap().getFileDirtyScope(document, Pass.UPDATE_ALL) == null) {
                        final MarkupModelImpl markupModel = (MarkupModelImpl) DocumentMarkupModel.forDocument(document, project, true);
                        markupModel.addMarkupModelListener(disposable, markupModelListener);
                        messageBusConnection.disconnect();
                    }
                }
            });
        });

        return markupModelListener;
    }

    @Override
    public void afterAdded(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        if (rangeHighlighterEx.getLayer() == HighlighterLayer.WARNING) {
            this.highlightersPerType.add(new StaticAnalysisWarning(StaticAnalysisType.UNKNOWN, TrackingEventType.SA_WARNING_CREATED));
        }
    }

    @Override
    public void beforeRemoved(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        if (rangeHighlighterEx.getLayer() == HighlighterLayer.WARNING) {
            this.highlightersPerType.add(new StaticAnalysisWarning(StaticAnalysisType.UNKNOWN, TrackingEventType.SA_WARNING_REMOVED));
        }
    }

    @Override
    public void attributesChanged(@NotNull RangeHighlighterEx rangeHighlighterEx, boolean b, boolean b1) {
    }

    @Override
    public void dispose() {
        this.timerTask.run();
        this.timer.cancel();
    }

    /**
     * Data holder class to store the multiple values required to track an event for.
     */
    private class StaticAnalysisWarning {
        private final StaticAnalysisType staticAnalysisType;
        private final TrackingEventType trackingEventType;
        DateTime creationTime;

        StaticAnalysisWarning(StaticAnalysisType staticAnalysisType, TrackingEventType trackingEventType) {
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
