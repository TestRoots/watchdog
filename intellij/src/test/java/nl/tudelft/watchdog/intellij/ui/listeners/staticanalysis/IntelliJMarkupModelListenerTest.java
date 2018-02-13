package nl.tudelft.watchdog.intellij.ui.listeners.staticanalysis;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInspection.ex.InspectionToolRegistrar;
import com.intellij.codeInspection.unusedImport.UnusedImportInspection;
import com.intellij.ide.startup.StartupManagerEx;
import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.util.Disposer;
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.InspectionsKt;
import com.intellij.testFramework.TestSourceBasedTestCase;
import com.siyeh.ig.controlflow.IfStatementWithIdenticalBranchesInspection;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis.IntelliJMarkupModelListener;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IntelliJMarkupModelListenerTest extends TestSourceBasedTestCase {

    private DaemonCodeAnalyzerImpl codeAnalyzer;
    private Disposable disposable;
    private IntelliJMarkupModelListener markupListener;
    private TrackingEventManager trackingEventManager;
    private PsiFile testFile;
    private Document document;
    private TextEditor textEditor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.disposable = Disposer.newDisposable();
        this.trackingEventManager = Mockito.mock(TrackingEventManager.class);

        this.testFile = getContentDirectory().findFile("Main.java");
        this.document = PsiDocumentManager.getInstance(getProject()).getDocument(Objects.requireNonNull(testFile));
        Editor editor = FileEditorManager.getInstance(getProject()).openTextEditor(new OpenFileDescriptor(getProject(), this.testFile.getVirtualFile(), 0), true);
        assert editor != null;
        this.textEditor = TextEditorProvider.getInstance().getTextEditor(editor);

        markupListener = IntelliJMarkupModelListener.initializeAfterAnalysisFinished(
                getProject(),
                this.disposable,
                document,
                trackingEventManager
        );

        codeAnalyzer = (DaemonCodeAnalyzerImpl) DaemonCodeAnalyzerImpl.getInstance(getProject());
        codeAnalyzer.prepareForTest();

        final StartupManagerImpl startupManager = (StartupManagerImpl) StartupManagerEx.getInstanceEx(getProject());
        startupManager.runStartupActivities();
        startupManager.startCacheUpdate();
        startupManager.runPostStartupActivities();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.disposable.dispose();
        codeAnalyzer.cleanupAfterTest();
    }

    @Nullable
    @Override
    protected String getTestPath() {
        return "staticanalysis";
    }

    @Override
    protected String getTestDataPath() {
        return this.getClass().getResource("/fixtures").getPath();
    }

    public void test() {
        this.runCodeAnalyzerOnFile();
    }

    private void runCodeAnalyzerOnFile() {
        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
        InspectionsKt.createProfile(ProjectInspectionProfileManager.getInstance(getProject()), new IfStatementWithIdenticalBranchesInspection(), disposable);
        final List<HighlightInfo> highlightInfos = codeAnalyzer
                .runPasses(this.testFile, this.document, Collections.singletonList(this.textEditor), new int[0], false, () -> {
                });
        System.out.println(highlightInfos.size());
        System.out.println(DaemonCodeAnalyzerEx.getInstanceEx(getProject()).getFileLevelHighlights(getProject(), this.testFile));
    }
}
