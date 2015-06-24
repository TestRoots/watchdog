package nl.tudelft.watchdog.logic.ui.listeners;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import nl.tudelft.watchdog.logic.ui.EventManager;

import java.util.HashMap;
import java.util.Map;

public class EditorWindowListener implements EditorFactoryListener {
    private EventManager eventManager;

    private EditorFocusListener focusListener;

    private Map<Editor, EditorListener> editorListenerMap = new HashMap<Editor, EditorListener>();

    public EditorWindowListener (EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void editorCreated(EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        focusListener = new EditorFocusListener(eventManager, editor);
        editor.getContentComponent().addFocusListener(focusListener);
        editorListenerMap.put(editor, new EditorListener(eventManager, editor));
    }

    @Override
    public void editorReleased(EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        editor.getContentComponent().removeFocusListener(focusListener);
        editorListenerMap.remove(editor).removeListeners();
    }
}
