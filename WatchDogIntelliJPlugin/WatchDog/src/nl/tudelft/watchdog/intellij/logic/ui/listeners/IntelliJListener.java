package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import nl.tudelft.watchdog.intellij.logic.ui.EventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;

/**
 * Sets up the listeners for IntelliJ UI events and registers the shutdown
 * listeners.
 */
public class IntelliJListener {
    
    /** The editorObservable */
    private EventManager eventManager;

    private Project project;

    /** Dummy disposable, needed for EditorFactory listener */
    private Disposable parent;

    private final MessageBusConnection connection;

    private EditorWindowListener editorWindowListener;

    private GeneralActivityListener activityListener;

    /** Constructor. */
    public IntelliJListener(EventManager eventManager, Project project) {
        this.eventManager = eventManager;
        this.project = project;

        parent = new Disposable() {
            @Override
            public void dispose() {
                // intentionally left empty
            }
        };

        editorWindowListener = new EditorWindowListener(eventManager, project.getName());

        final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect();
    }

    /**
     * Adds IntelliJ listeners including already opened windows and
     * registers shutdown and debugger listeners.
     */
    public void attachListeners() {
        eventManager.update(new WatchDogEvent(this, EventType.START_IDE));

        connection.subscribe(ApplicationActivationListener.TOPIC,
                new IntelliJActivationListener(eventManager));
        activityListener = new GeneralActivityListener(eventManager, project.getName());

        EditorFactory.getInstance().addEditorFactoryListener(editorWindowListener, parent);
        DebuggerManagerEx.getInstanceEx(project).addDebuggerManagerListener(new DebuggerListener(eventManager));
    }

    public void removeListeners() {
        connection.disconnect();
        activityListener.removeListeners();
        // Disposing this parent should remove EditorFactory listener with a delay
        parent.dispose();
        // Deprecated, but removes listener immediately
        EditorFactory.getInstance().removeEditorFactoryListener(editorWindowListener);
    }
}
