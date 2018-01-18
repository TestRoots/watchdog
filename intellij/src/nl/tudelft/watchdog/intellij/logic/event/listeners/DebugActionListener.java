package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.xdebugger.impl.actions.*;
import com.intellij.xdebugger.impl.ui.tree.actions.XInspectAction;
import com.intellij.xdebugger.impl.ui.tree.actions.XSetValueAction;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

import java.util.Date;

/**
 * Listener for derivations of {@link AnAction}s related to the debugger. On receipt of these actions, the appropriate events are generated and given to the event manager.
 */
public class DebugActionListener implements AnActionListener {

    /**
     * TrackingEventManager that will be used to persist the new events resulting from actions.
     */
    private final TrackingEventManager trackingEventManager;

    /**
     * Constructor.
     */
    public DebugActionListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
    }

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        // intentionally left empty
    }

    @Override
    public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (anAction instanceof StepIntoAction || anAction instanceof ForceStepIntoAction) {
            createEvent(TrackingEventType.STEP_INTO);
        } else if (anAction instanceof StepOverAction || anAction instanceof ForceStepOverAction) {
            createEvent(TrackingEventType.STEP_OVER);
        } else if (anAction instanceof StepOutAction) {
            createEvent(TrackingEventType.STEP_OUT);
        } else if (anAction instanceof XInspectAction) {
            createEvent(TrackingEventType.INSPECT_VARIABLE);
        } else if (anAction.getClass().getSimpleName().equals("XAddToWatchesAction")) {
            createEvent(TrackingEventType.DEFINE_WATCH);
        } else if (anAction.getClass().getSimpleName().equals("EvaluateAction")) {
            createEvent(TrackingEventType.EVALUATE_EXPRESSION);
        } else if (anAction instanceof XSetValueAction) {
            createEvent(TrackingEventType.MODIFY_VARIABLE_VALUE);
        }
    }

    private void createEvent(TrackingEventType trackingEventType) {
        trackingEventManager.addEvent(new DebugEventBase(trackingEventType,new Date()));
    }

    @Override
    public void beforeEditorTyping(char c, DataContext dataContext) {
        // intentionally left empty
    }
}
