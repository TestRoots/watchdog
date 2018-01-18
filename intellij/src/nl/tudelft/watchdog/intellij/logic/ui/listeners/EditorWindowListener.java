package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.impl.MarkupModelImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EditorWindowListener implements EditorFactoryListener {
    private WatchDogEventManager eventManager;

    private EditorFocusListener focusListener;

    private Map<Editor, Disposable> editorDisposableMap = new HashMap<>();

    private Project project;

    public EditorWindowListener (WatchDogEventManager eventManager, Project project) {
        this.eventManager = eventManager;
        this.project = project;
    }

    @Override
    public void editorCreated(EditorFactoryEvent editorFactoryEvent) {
        if(editorBelongsToThisProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        focusListener = new EditorFocusListener(eventManager, editor);
        editor.getContentComponent().addFocusListener(focusListener);

        Disposable parentDisposable = Disposer.newDisposable();

        final MarkupModelImpl markupModel = (MarkupModelImpl) DocumentMarkupModel.forDocument(editor.getDocument(), project, true);
        markupModel.addMarkupModelListener(parentDisposable, new IntelliJMarkupModelListener());

        Disposer.register(parentDisposable, new EditorListener(eventManager, editor));

        editorDisposableMap.put(editor, parentDisposable);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent editorFactoryEvent) {
        if(editorBelongsToThisProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        editor.getContentComponent().removeFocusListener(focusListener);
        Disposer.dispose(editorDisposableMap.remove(editor));
    }

    private boolean editorBelongsToThisProject(EditorFactoryEvent editorFactoryEvent) {
        try {
            return !editorFactoryEvent.getEditor().getProject().getName().equals(project.getName());
        }
        catch(NullPointerException e) {
            return true;
        }
    }

}
