package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import com.intellij.openapi.Disposable;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import java.awt.*;
import java.awt.event.*;

/**
 * A listener that determines whether there was general activity in the IntelliJ
 * window. It cleans up all its child listeners in {@link #dispose()}.
 */
public class GeneralActivityListener implements Disposable {

    private AWTEventListener mouseActivityListener;
    private AWTEventListener keyboardActivityListener;


    /** Constructor. */
    public GeneralActivityListener(final String projectName) {
        mouseActivityListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!WatchDogUtils.getProjectName().equals(projectName)) {
                    return;
                }
                 WatchDogEventType.USER_ACTIVITY.process(event);
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(mouseActivityListener, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);

        keyboardActivityListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!WatchDogUtils.getProjectName().equals(projectName)) {
                    return;
                }
                switch (((KeyEvent) event).getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_PAGE_DOWN:
                    case KeyEvent.VK_PAGE_UP:
                        WatchDogEventType.USER_ACTIVITY.process(event);
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(keyboardActivityListener, AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public void dispose() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(mouseActivityListener);
        Toolkit.getDefaultToolkit().removeAWTEventListener(keyboardActivityListener);
    }
}
