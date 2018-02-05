package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.xdebugger.XDebuggerManager;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.intellij.logic.event.listeners.BreakpointListener;
import nl.tudelft.watchdog.intellij.logic.event.listeners.DebugActionListener;
import nl.tudelft.watchdog.intellij.logic.event.listeners.DebugEventListener;

/**
 * Sets up the listeners for IntelliJ UI events and registers the shutdown
 * listeners. It cleans up all its child listeners in {@link #dispose()}.
 */
public class IntelliJListener implements Disposable {
    
    /** The editorObservable */
    private TrackingEventManager trackingEventManager;

    private Project project;

    private final MessageBusConnection connection;

    private EditorWindowListener editorWindowListener;

    private DebuggerListener debuggerManagerListener;
    private BreakpointListener xBreakpointListener;
    private DebugEventListener debuggerContextListener;
    private DebugActionListener debuggerActionListener;

    /** Constructor. */
    public IntelliJListener(TrackingEventManager trackingEventManager, Project project) {
        this.trackingEventManager = trackingEventManager;
        this.project = project;

        editorWindowListener = new EditorWindowListener(project, trackingEventManager);

        final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect();
        Disposer.register(this, connection);

        this.attachListeners();
    }

    /**
     * Adds IntelliJ listeners including already opened windows and
     * registers shutdown and debugger listeners.
     */
    private void attachListeners() {
        WatchDogEventType.START_IDE.process(this);

        // Most of the listener APIs in IntelliJ accept a `Disposable` argument.
        // Whenever you want to add a Listener to something, you have to supply a `Disposable`.
        // Whenever this `Disposable` is disposed, the listener is automatically removed.
        //
        // Sadly, not all listener methods follow this pattern. `addEditorFactoryListener` does,
        // but `addAWTEventListener` as used in `GeneralActivityListener` does not.
        //
        // Lastly, the messageBus (`connection`) is already registered in the constructor
        connection.subscribe(ApplicationActivationListener.TOPIC,
                new IntelliJActivationListener());
        Disposer.register(this, new GeneralActivityListener(project.getName()));
        EditorFactory.getInstance().addEditorFactoryListener(editorWindowListener, this);
        attachDebuggerListeners();
    }

    private void attachDebuggerListeners() {
        debuggerManagerListener = new DebuggerListener();
        xBreakpointListener = new BreakpointListener(trackingEventManager);
        debuggerContextListener = new DebugEventListener(trackingEventManager);
        debuggerActionListener = new DebugActionListener(trackingEventManager);

        DebuggerManagerEx.getInstanceEx(project).addDebuggerManagerListener(debuggerManagerListener);
        XDebuggerManager.getInstance(project).getBreakpointManager().addBreakpointListener(xBreakpointListener);
        DebuggerManagerEx.getInstanceEx(project).getContextManager().addListener(debuggerContextListener);
        ActionManager.getInstance().addAnActionListener(debuggerActionListener);
    }

    @Override
    public void dispose() {
        DebuggerManagerEx.getInstanceEx(project).removeDebuggerManagerListener(debuggerManagerListener);
        XDebuggerManager.getInstance(project).getBreakpointManager().removeBreakpointListener(xBreakpointListener);
        DebuggerManagerEx.getInstanceEx(project).getContextManager().removeListener(debuggerContextListener);
        ActionManager.getInstance().removeAnActionListener(debuggerActionListener);
    }
}
