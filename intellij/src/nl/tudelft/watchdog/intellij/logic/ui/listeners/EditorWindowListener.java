package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.impl.MarkupModelImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EditorWindowListener implements EditorFactoryListener, Disposable {

    private EditorFocusListener focusListener;

    private Map<Editor, Disposable> editorListenerMap = new HashMap<>();

    private Project project;
    private TrackingEventManager trackingEventManager;

    EditorWindowListener(Project project, TrackingEventManager trackingEventManager) {
        this.project = project;
        this.trackingEventManager = trackingEventManager;
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
        final MarkupModelImpl markupModel = (MarkupModelImpl) DocumentMarkupModel.forDocument(editor.getDocument(), this.project, true);
        markupModel.addMarkupModelListener(parentDisposable, new IntelliJMarkupModelListener(trackingEventManager));

        editorListenerMap.put(editor, parentDisposable);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent editorFactoryEvent) {
        if (editorDoesNotBelongToProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        editor.getContentComponent().removeFocusListener(focusListener);

        Disposer.dispose(editorListenerMap.remove(editor));
    }

    private boolean editorDoesNotBelongToProject(EditorFactoryEvent editorFactoryEvent) {
        return !project.equals(editorFactoryEvent.getEditor().getProject());
    }

    @Override
    public void dispose() {
        editorListenerMap.keySet()
                .forEach(editor ->Disposer.dispose(editorListenerMap.remove(editor)));
    }
}