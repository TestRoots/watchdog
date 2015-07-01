package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;

import java.awt.*;
import java.awt.event.*;

/**
 * A listener that determines whether there was general activity in the IntelliJ
 * window.
 */
public class GeneralActivityListener {

    private AWTEventListener mouseActivityListener;
    private AWTEventListener keyboardActivityListener;


    /**
     * Constructor.
     */
    public GeneralActivityListener(final EventManager eventManager) {
        // Mouse Events
        mouseActivityListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                eventManager.update(new WatchDogEvent(event, EventType.USER_ACTIVITY));
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(mouseActivityListener, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);

        keyboardActivityListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (((KeyEvent) event).getKeyCode() == KeyEvent.VK_RIGHT ||
                        ((KeyEvent) event).getKeyCode() == KeyEvent.VK_UP ||
                        ((KeyEvent) event).getKeyCode() == KeyEvent.VK_DOWN ||
                        ((KeyEvent) event).getKeyCode() == KeyEvent.VK_LEFT ||
                        ((KeyEvent) event).getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
                        ((KeyEvent) event).getKeyCode() == KeyEvent.VK_PAGE_UP
                        ) {
                    eventManager.update(new WatchDogEvent(event, EventType.USER_ACTIVITY));
                }
            }
        };
        // Keyboard Events
        Toolkit.getDefaultToolkit().addAWTEventListener(keyboardActivityListener, AWTEvent.KEY_EVENT_MASK);
    }

    public void removeListeners() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(mouseActivityListener);
        Toolkit.getDefaultToolkit().removeAWTEventListener(keyboardActivityListener);
    }
}
