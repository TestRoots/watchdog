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
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CoreMarkupModelListener;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.FileWarningSnapshotEvent;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.Warning;
import nl.tudelft.watchdog.intellij.logic.document.DocumentCreator;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier.IDE_BUNDLE;

public class IntelliJMarkupModelListener extends CoreMarkupModelListener implements MarkupModelListener, Disposable {

    static {
        IDE_BUNDLE.createPatternsForKeysInBundle("messages.InspectionsBundle");
        IDE_BUNDLE.createPatternsForKeysInBundle("com.siyeh.InspectionGadgetsBundle");

        IDE_BUNDLE.sortList();
    }

    private final Document document;
    private final TrackingEventManager trackingEventManager;
    private final com.intellij.openapi.editor.Document intellijDocument;

    private final Set<Warning<RangeHighlighterEx>> generatedWarnings;
    private final Set<Warning<RangeHighlighterEx>> warnings;
    private final Map<RangeHighlighterEx, DateTime> timeMapping;

    private IntelliJMarkupModelListener(Document document, TrackingEventManager trackingEventManager, com.intellij.openapi.editor.Document intellijDocument) {
        this.document = document;
        this.trackingEventManager = trackingEventManager;
        this.intellijDocument = intellijDocument;

        generatedWarnings = new HashSet<>();
        warnings = new HashSet<>();
        timeMapping = new WeakHashMap<>();
    }

    /**
     * Create a new {@link IntelliJMarkupModelListener} for an editor. This listener is only attached after the
     * {@link DaemonCodeAnalyzer} has finished analyzing this file.
     *
     * The listener is attached to the {@link com.intellij.openapi.editor.markup.MarkupModel} of the document,
     * which contains all {@link RangeHighlighterEx}s that represent the Static Analysis warnings.
     *
     * @param project The project the file exists in.
     * @param disposable The disposable to clean up the listeners and any potential {@link MessageBusConnection}.
     * @param editor The editor of the document.
     * @param trackingEventManager The manager that can process all the events generated.
     * @return A newly initiated listener that will later be attached to the {@link com.intellij.openapi.editor.markup.MarkupModel} of the document
     */
    public static IntelliJMarkupModelListener initializeAfterAnalysisFinished(
            Project project, Disposable disposable, Editor editor, TrackingEventManager trackingEventManager) {

        final com.intellij.openapi.editor.Document intellijDocument = editor.getDocument();
        final IntelliJMarkupModelListener markupModelListener = new IntelliJMarkupModelListener(DocumentCreator.createDocument(editor), trackingEventManager, intellijDocument);

        // We need to run this in smart mode, because the very first time you start your editor, it is very briefly
        // in dumb mode and the codeAnalyzer thinks (incorrectly) it is  finished.
        // Therefore, wait for smart mode and only then start listening, to make sure the codeAnalyzer actually did its thing.
        // For more information see https://www.jetbrains.org/intellij/sdk/docs/basics/indexing_and_psi_stubs.html
        DumbServiceImpl.getInstance(project).runWhenSmart(() -> {
        	// In the case that the analyzer finished after a file was closed just after opening the IDE,
			// do not try to attach any other listeners. These listeners would fail, as the editor no longer exists.
        	if (Disposer.isDisposed(disposable)) {
        		return;
			}

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

                        markupModelListener.processWarningSnapshot(markupModel.getAllHighlighters());
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

    private void processWarningSnapshot(RangeHighlighter[] highlighters) {
        if (highlighters.length == 0) {
            return;
        }

        // markupModel.getAllHighlighters returns a list of RangeHighlighter,
        // even though we know they are all instances of RangeHighlighterEx.
        // Therefore do an instanceof check and then explicitly cast them
        // to use them in the other methods
        final Stream<RangeHighlighterEx> rangeHighlighters = Arrays.stream(highlighters)
                .filter(RangeHighlighterEx.class::isInstance)
                .map(RangeHighlighterEx.class::cast);

        final List<Warning<String>> warnings = rangeHighlighters
                .filter(IntelliJMarkupModelListener::isWarningRangeHighlighter)
                .map(rangeHighlighter -> createWarningFromRangeHighlighter(rangeHighlighter, null))
                .map(IntelliJMarkupModelListener::classifyWarning)
                .collect(Collectors.toList());

        this.trackingEventManager.addEvent(new FileWarningSnapshotEvent(this.document.prepareDocument(), warnings));
    }

    private void flushForDocument() {
        addCreatedWarnings(this.trackingEventManager, this.generatedWarnings.stream().map(IntelliJMarkupModelListener::classifyWarning), this.document);
        this.generatedWarnings.clear();

        addRemovedWarnings(this.trackingEventManager, this.warnings.stream().map(IntelliJMarkupModelListener::classifyWarning), this.document);
        this.warnings.clear();
    }

    @Override
    public void afterAdded(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        if (isWarningRangeHighlighter(rangeHighlighterEx)) {
            final DateTime creationTime = DateTime.now();

            this.timeMapping.put(rangeHighlighterEx, creationTime);
            this.generatedWarnings.add(new Warning<>(rangeHighlighterEx, getLineNumberForHighlighter(rangeHighlighterEx), creationTime.toDate()));
        }
    }

    @Override
    public void beforeRemoved(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        DateTime creationTime = this.timeMapping.remove(rangeHighlighterEx);

        if (isWarningRangeHighlighter(rangeHighlighterEx)) {
            // Only process a deletion if we hadn't encountered this marker in this session before.
            // If we did encounter it, remove returns `true` and the warning is not saved as removed.
            if (!this.generatedWarnings.removeIf(warning -> warning.type.equals(rangeHighlighterEx))) {
                this.warnings.add(createWarningFromRangeHighlighter(rangeHighlighterEx, creationTime));
            }
        }
    }

    @Override
    public void attributesChanged(@NotNull RangeHighlighterEx rangeHighlighterEx, boolean b, boolean b1) {
        // Unused for now.
    }

    @Override
    public void dispose() {
        // We store no internal state ourselves.
    }

    private static boolean isWarningRangeHighlighter(@NotNull RangeHighlighterEx rangeHighlighterEx) {
        return rangeHighlighterEx.getLayer() == HighlighterLayer.WARNING;
    }

    @NotNull
    private Warning<RangeHighlighterEx> createWarningFromRangeHighlighter(@NotNull RangeHighlighterEx rangeHighlighterEx, DateTime creationTime) {
        final DateTime now = DateTime.now();

        int seconds;

        if (creationTime == null) {
            seconds = -1;
        } else {
            seconds = Seconds.secondsBetween(creationTime, now).getSeconds();
        }

        return new Warning<>(
                rangeHighlighterEx,
                getLineNumberForHighlighter(rangeHighlighterEx),
                now.toDate(),
                seconds
        );
    }

    private int getLineNumberForHighlighter(RangeHighlighterEx warning) {
        try {
            return intellijDocument.getLineNumber(warning.getAffectedAreaStartOffset());
            // It could be that IntelliJ already synced the document before generating the
            // "beforeRemoved" event. Therefore, catch these errors and set to -1 as we have
            // no clue what the actual line number was.
        } catch (IndexOutOfBoundsException ignored) {
            return -1;
        }
    }

    private static Warning<String> classifyWarning(Warning<RangeHighlighterEx> warning) {
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
