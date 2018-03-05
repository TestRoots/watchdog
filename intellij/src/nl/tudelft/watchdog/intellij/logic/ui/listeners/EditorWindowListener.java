package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis.IntelliJMarkupModelListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EditorWindowListener implements EditorFactoryListener, Disposable {

    private EditorFocusListener focusListener;

    /**
     * We associate every editor that is created with a tree of disposables.
     * The parent {@link Disposable} is registered with each associated listener to
     * events of the editor. By using Disposable for this structure, we can
     * properly cleanup all listeners once we dispose ourselves.
     */
    private final Map<Editor, Disposable> editorDisposableTreeMap;

    private final Project project;
    private final TrackingEventManager trackingEventManager;

    EditorWindowListener(Project project, TrackingEventManager trackingEventManager) {
        this.project = project;
        this.trackingEventManager = trackingEventManager;

        this.editorDisposableTreeMap = new HashMap<>();
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent editorFactoryEvent) {
        if(editorDoesNotBelongToProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        focusListener = new EditorFocusListener(editor);
        editor.getContentComponent().addFocusListener(focusListener);

        Disposable parentDisposable = Disposer.newDisposable();
        Disposer.register(parentDisposable, new EditorListener(editor));
        Disposer.register(parentDisposable, IntelliJMarkupModelListener.initializeAfterAnalysisFinished(project, parentDisposable, editor, trackingEventManager));

        editorDisposableTreeMap.put(editor, parentDisposable);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent editorFactoryEvent) {
        if (editorDoesNotBelongToProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        editor.getContentComponent().removeFocusListener(focusListener);

        Disposer.dispose(editorDisposableTreeMap.remove(editor));
    }

    private boolean editorDoesNotBelongToProject(EditorFactoryEvent editorFactoryEvent) {
        return !project.equals(editorFactoryEvent.getEditor().getProject());
    }

    @Override
    public void dispose() {
        editorDisposableTreeMap.keySet()
                .forEach(editor -> Disposer.dispose(editorDisposableTreeMap.remove(editor)));
    }
}
