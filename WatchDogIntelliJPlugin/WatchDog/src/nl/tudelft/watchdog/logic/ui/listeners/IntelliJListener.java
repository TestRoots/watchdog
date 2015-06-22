package nl.tudelft.watchdog.logic.ui.listeners;

import com.intellij.execution.console.RunIdeConsoleAction;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType;

import java.awt.*;

/**
 * Sets up the listeners for IntelliJ UI events and registers the shutdown
 * listeners.
 */
public class IntelliJListener {
    /**
     * The editorObservable.
     */
    private EventManager eventManager;

    private Disposable parent; //dummy disposable

    private final MessageBusConnection connection;

    private EditorWindowListener editorListener;
    private GeneralActivityListener activityListener;

    /**
     * Constructor.
     */
    public IntelliJListener(EventManager userActionManager) {
        this.eventManager = userActionManager;

        parent = new Disposable() {
            @Override
            public void dispose() {
                // intentionally left empty
            }
        };

        editorListener = new EditorWindowListener(eventManager);

        final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect();
    }

    /**
     * Adds IntelliJ listeners including already opened windows and
     * registers shutdown listeners.
     */
    public void attachListeners() {
        eventManager.update(new WatchDogEvent(this, EventType.START_INTELLIJ));

        connection.subscribe(ApplicationActivationListener.TOPIC,
                new IntelliJActivationListener(eventManager));
        activityListener = new GeneralActivityListener(eventManager);

        EditorFactory.getInstance().addEditorFactoryListener(editorListener, parent);
    }

    public void removeListeners() {

        connection.disconnect();
        activityListener.removeListeners();
        parent.dispose(); // removing EditorFactoryListener
        EditorFactory.getInstance().removeEditorFactoryListener(editorListener);
    }
}
