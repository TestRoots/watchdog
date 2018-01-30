package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.editor.Editor;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class EditorFocusListener implements FocusListener {

    private final Editor myEditor;

    public EditorFocusListener(Editor editor) {
        this.myEditor = editor;
    }

    @Override
    public void focusGained(FocusEvent e) {
        WatchDogEventType.ACTIVE_FOCUS.process(myEditor);
    }

    @Override
    public void focusLost(FocusEvent e) {
        WatchDogEventType.INACTIVE_FOCUS.process(myEditor);
    }
}
