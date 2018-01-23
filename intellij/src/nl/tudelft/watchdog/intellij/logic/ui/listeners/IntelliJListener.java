package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.xdebugger.XDebuggerManager;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.intellij.logic.event.listeners.BreakpointListener;
import nl.tudelft.watchdog.intellij.logic.event.listeners.DebugActionListener;
import nl.tudelft.watchdog.intellij.logic.event.listeners.DebugEventListener;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;

/**
 * Sets up the listeners for IntelliJ UI events and registers the shutdown
 * listeners.
 */
public class IntelliJListener {
    
    /** The editorObservable */
    private WatchDogEventManager watchDogEventManager;
    private TrackingEventManager trackingEventManager;

    private Project project;

    /** Dummy disposable, needed for EditorFactory listener */
    private Disposable parent;

    private final MessageBusConnection connection;

    private EditorWindowListener editorWindowListener;

    private GeneralActivityListener activityListener;

    /** Constructor. */
    public IntelliJListener(WatchDogEventManager watchDogEventManager, TrackingEventManager trackingEventManager, Project project) {
        this.watchDogEventManager = watchDogEventManager;
        this.trackingEventManager = trackingEventManager;
        this.project = project;

        parent = new Disposable() {
            @Override
            public void dispose() {
                // intentionally left empty
            }
        };

        editorWindowListener = new EditorWindowListener(watchDogEventManager, project.getName());

        final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect();
    }

    /**
     * Adds IntelliJ listeners including already opened windows and
     * registers shutdown and debugger listeners.
     */
    public void attachListeners() {
        watchDogEventManager.update(new WatchDogEvent(this, EventType.START_IDE));

        connection.subscribe(ApplicationActivationListener.TOPIC,
                new IntelliJActivationListener(watchDogEventManager));
        activityListener = new GeneralActivityListener(watchDogEventManager, project.getName());
        EditorFactory.getInstance().addEditorFactoryListener(editorWindowListener, parent);
        attachDebuggerListeners();
    }

    private void attachDebuggerListeners() {
        DebuggerManagerEx.getInstanceEx(project).addDebuggerManagerListener(new DebuggerListener(watchDogEventManager));
        XDebuggerManager.getInstance(project).getBreakpointManager().addBreakpointListener(new BreakpointListener(trackingEventManager));
        DebuggerManagerEx.getInstanceEx(project).getContextManager().addListener(new DebugEventListener(trackingEventManager));
        ActionManager.getInstance().addAnActionListener(new DebugActionListener(trackingEventManager));
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
