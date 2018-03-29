package nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis;

import com.intellij.AppTopics;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.impl.MarkupModelImpl;
import com.intellij.openapi.editor.impl.event.MarkupModelListener;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.listeners.CoreMarkupModelListener;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier;
import nl.tudelft.watchdog.intellij.logic.document.DocumentCreator;
import org.jetbrains.annotations.NotNull;
import org.jfree.data.time.Millisecond;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier.IDE_BUNDLE;

public class IntelliJMarkupModelListener extends CoreMarkupModelListener implements MarkupModelListener, Disposable {

    static {
        IDE_BUNDLE.createPatternsForKeysInBundle("messages.InspectionsBundle");
        IDE_BUNDLE.createPatternsForKeysInBundle("com.siyeh.InspectionGadgetsBundle");

        IDE_BUNDLE.sortList();
    }

    private final Set<Warning<RangeHighlighterEx>> generatedWarnings;
    private final Set<Warning<RangeHighlighterEx>> warnings;
    private final Map<RangeHighlighterEx, DateTime> timeMapping;
    private final com.intellij.openapi.editor.Document intellijDocument;

    private IntelliJMarkupModelListener(Document document, TrackingEventManager trackingEventManager, com.intellij.openapi.editor.Document intellijDocument) {
        super(document, trackingEventManager);
        this.intellijDocument = intellijDocument;
        generatedWarnings = new HashSet<>();
        warnings = new HashSet<>();
        timeMapping = new WeakHashMap<>();
    }

    public static IntelliJMarkupModelListener initializeAfterAnalysisFinished(
            Project project, Disposable disposable, Editor editor, TrackingEventManager trackingEventManager) {

        final com.intellij.openapi.editor.Document intellijDocument = editor.getDocument();
        final IntelliJMarkupModelListener markupModelListener = new IntelliJMarkupModelListener(DocumentCreator.createDocument(editor), trackingEventManager, intellijDocument);

        // We need to run this in smart mode, because the very first time you start your editor, it is very briefly
        // in dumb mode and the codeAnalyzer thinks (incorrectly) it is  finished.
        // Therefore, wait for smart mode and only then start listening, to make sure the codeAnalyzer actually did its thing.
        DumbServiceImpl.getInstance(project).runWhenSmart(() -> {
            final DaemonCodeAnalyzerImpl analyzer = (DaemonCodeAnalyzerImpl) DaemonCodeAnalyzer.getInstance(project);
            final MessageBusConnection codeAnalyzerMessageBusConnection = project.getMessageBus().connect(disposable);
            final MessageBusConnection documentMessageBusConnection = project.getMessageBus().connect(disposable);

            codeAnalyzerMessageBusConnection.subscribe(DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC, new DaemonCodeAnalyzer.DaemonListenerAdapter() {
                @Override
                public void daemonFinished() {
                    // We only receive global Code Analyzer events. This means that once such an event triggered,
                    // we do not know for which file the analysis actually succeeded. Therefore we need to check
                    // the so-called "dirty" state of the file for this particular analyzer to check whether
                    // the analyzer actually finished. In this case, `null` indicates: the file is not dirty.
                    if (analyzer.getFileStatusMap().getFileDirtyScope(intellijDocument, Pass.UPDATE_ALL) == null) {
                        final MarkupModelImpl markupModel = (MarkupModelImpl) DocumentMarkupModel.forDocument(intellijDocument, project, true);
                        markupModel.addMarkupModelListener(disposable, markupModelListener);

                        // We batch up changes and only transfer them on every save. This is in-line with the Eclipse
                        // interface, which only exposes listeners for `POST_BUILD`. Therefore, cache all warnings in
                        // {@link IntelliJMarkupModelListener#generatedWarnings} and {@link IntelliJMarkupModelListener#warnings}
                        // and flush these warnings after the fact.
                        documentMessageBusConnection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerAdapter() {
                            @Override
                            public void beforeDocumentSaving(@NotNull com.intellij.openapi.editor.Document savedDocument) {
                                if (intellijDocument.equals(savedDocument)) {
                                    markupModelListener.flushForDocument();
                                }
                            }
                        });
                        codeAnalyzerMessageBusConnection.disconnect();
                    }
                }
            });
        });

        return markupModelListener;
    }

    private void flushForDocument() {
        addCreatedWarnings(this.generatedWarnings.stream().map(this::classifyWarning));
        this.generatedWarnings.clear();

        addRemovedWarnings(this.warnings.stream().map(this::classifyWarning));
        this.warnings.clear();
    }

    @Override
    public void afterAdded(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        if (rangeHighlighterEx.getLayer() == HighlighterLayer.WARNING) {
            final DateTime creationTime = DateTime.now();

            this.timeMapping.put(rangeHighlighterEx, creationTime);
            this.generatedWarnings.add(new Warning<>(rangeHighlighterEx, getLineNumberForHighlighter(rangeHighlighterEx), creationTime));
        }
    }

    @Override
    public void beforeRemoved(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        DateTime creationTime = this.timeMapping.remove(rangeHighlighterEx);

        if (rangeHighlighterEx.getLayer() == HighlighterLayer.WARNING) {
            // Only process a deletion if we hadn't encountered this marker in this session before.
            // If we did encounter it, remove returns `true` and the warning is not saved as removed.
            if (!this.generatedWarnings.removeIf(warning -> warning.type.equals(rangeHighlighterEx))) {
                final DateTime now = DateTime.now();

                this.warnings.add(new Warning<>(
                        rangeHighlighterEx,
                        getLineNumberForHighlighter(rangeHighlighterEx),
                        now,
                        creationTime == null ? -1 : Seconds.secondsBetween(creationTime, now).getSeconds()
                ));
            }
        }
    }

    @Override
    public void attributesChanged(@NotNull RangeHighlighterEx rangeHighlighterEx, boolean b, boolean b1) {
    }

    @Override
    public void dispose() {
    }

    private int getLineNumberForHighlighter(RangeHighlighterEx warning) {
        return intellijDocument.getLineNumber(warning.getAffectedAreaStartOffset());
    }

    private Warning<String> classifyWarning(Warning<RangeHighlighterEx> warning) {
        return new Warning<>(classifyWarningTypeFromHighlighter(warning.type), warning.lineNumber, warning.warningCreationTime, warning.secondsBetween);
    }

    @NotNull
    private static String classifyWarningTypeFromHighlighter(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        final Object errorStripeTooltip = rangeHighlighterEx.getErrorStripeTooltip();

        if (errorStripeTooltip instanceof HighlightInfo) {
            return StaticAnalysisMessageClassifier.classify(((HighlightInfo) errorStripeTooltip).getDescription());
        }

        return "unknown";
    }
}
