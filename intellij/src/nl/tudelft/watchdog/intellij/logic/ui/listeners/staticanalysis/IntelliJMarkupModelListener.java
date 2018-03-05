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
import nl.tudelft.watchdog.intellij.logic.document.DocumentCreator;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class IntelliJMarkupModelListener extends CoreMarkupModelListener implements MarkupModelListener, Disposable {

    private final Set<RangeHighlighterEx> generatedWarnings;
    private final Set<RangeHighlighterEx> removedWarnings;

    private IntelliJMarkupModelListener(Document document, TrackingEventManager trackingEventManager) {
        super(document, trackingEventManager);
        generatedWarnings = new HashSet<>();
        removedWarnings = new HashSet<>();
    }

    public static IntelliJMarkupModelListener initializeAfterAnalysisFinished(
            Project project, Disposable disposable, Editor editor, TrackingEventManager trackingEventManager) {

        final com.intellij.openapi.editor.Document intellijDocument = editor.getDocument();
        final IntelliJMarkupModelListener markupModelListener = new IntelliJMarkupModelListener(DocumentCreator.createDocument(editor), trackingEventManager);

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
                        // {@link IntelliJMarkupModelListener#generatedWarnings} and {@link IntelliJMarkupModelListener#removedWarnings}
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
        addCreatedWarnings(this.generatedWarnings.stream().map(IntelliJMarkupModelListener::classifyWarningTypeFromHighlighter));
        this.generatedWarnings.clear();

        addRemovedWarnings(this.removedWarnings.stream().map(IntelliJMarkupModelListener::classifyWarningTypeFromHighlighter));
        this.removedWarnings.clear();
    }

    @Override
    public void afterAdded(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        if (rangeHighlighterEx.getLayer() == HighlighterLayer.WARNING) {
            this.generatedWarnings.add((rangeHighlighterEx));
        }
    }

    @Override
    public void beforeRemoved(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        if (rangeHighlighterEx.getLayer() == HighlighterLayer.WARNING) {
            // Only process a deletion if we hadn't encountered this marker in this session before.
            // If we did encounter it, remove returns `true` and the warning is not saved as removed.
            if (!this.generatedWarnings.remove(rangeHighlighterEx)) {
                this.removedWarnings.add(rangeHighlighterEx);
            }
        }
    }

    @Override
    public void attributesChanged(@NotNull RangeHighlighterEx rangeHighlighterEx, boolean b, boolean b1) {
    }

    @Override
    public void dispose() {
    }

    @NotNull
    private static String classifyWarningTypeFromHighlighter(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        final Object errorStripeTooltip = rangeHighlighterEx.getErrorStripeTooltip();

        if (errorStripeTooltip instanceof HighlightInfo) {
            return StaticAnalysisWarningClassifier.classify(((HighlightInfo) errorStripeTooltip).getDescription());
        }

        return "UNKNOWN";
    }
}
