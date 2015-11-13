package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import nl.tudelft.watchdog.intellij.logic.ui.EventManager;

import java.util.HashMap;
import java.util.Map;

public class EditorWindowListener implements EditorFactoryListener {
    private EventManager eventManager;

    private EditorFocusListener focusListener;

    private Map<Editor, EditorListener> editorListenerMap = new HashMap<Editor, EditorListener>();

    private String myProjectName;

    public EditorWindowListener (EventManager eventManager, String projectName) {
        this.eventManager = eventManager;
        myProjectName = projectName;
    }

    @Override
    public void editorCreated(EditorFactoryEvent editorFactoryEvent) {
        if(!editorBelongsToThisProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        focusListener = new EditorFocusListener(eventManager, editor);
        editor.getContentComponent().addFocusListener(focusListener);
        editorListenerMap.put(editor, new EditorListener(eventManager, editor));
    }

    @Override
    public void editorReleased(EditorFactoryEvent editorFactoryEvent) {
        if(!editorBelongsToThisProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        editor.getContentComponent().removeFocusListener(focusListener);
        editorListenerMap.remove(editor).removeListeners();
    }

    private boolean editorBelongsToThisProject(EditorFactoryEvent editorFactoryEvent) {
        return editorFactoryEvent.getEditor().getProject().getName().equals(myProjectName);
    }
}
