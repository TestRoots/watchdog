package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.editor.Editor;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.EditorEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class EditorFocusListener implements FocusListener {

    private final WatchDogEventManager eventManager;

    private final Editor myEditor;

    public EditorFocusListener(WatchDogEventManager eventManager, Editor editor) {
        this.eventManager = eventManager;
        this.myEditor = editor;
    }

    @Override
    public void focusGained(FocusEvent e) {
        eventManager.update(new EditorEvent(
                myEditor, WatchDogEvent.EventType.ACTIVE_FOCUS));
    }

    @Override
    public void focusLost(FocusEvent e) {
        eventManager.update(new EditorEvent(
                myEditor, WatchDogEvent.EventType.INACTIVE_FOCUS));
    }
}
