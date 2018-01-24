package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.editor.Editor;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.EditorEvent;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class EditorFocusListener implements FocusListener {

    private final Editor myEditor;

    public EditorFocusListener(Editor editor) {
        this.myEditor = editor;
    }

    @Override
    public void focusGained(FocusEvent e) {
        new EditorEvent(myEditor, WatchDogEvent.EventType.ACTIVE_FOCUS).update();
    }

    @Override
    public void focusLost(FocusEvent e) {
        new EditorEvent(myEditor, WatchDogEvent.EventType.INACTIVE_FOCUS).update();
    }
}
