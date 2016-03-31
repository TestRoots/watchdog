package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.xdebugger.impl.actions.*;
import com.intellij.xdebugger.impl.ui.tree.actions.XInspectAction;
import com.intellij.xdebugger.impl.ui.tree.actions.XSetValueAction;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventType;

import java.util.Date;

/**
 * Listener for debug related instances of {@link AnAction}. On receipt of these actions, the appropriate events are generated and given to the event manager.
 */
public class DebugActionListener implements AnActionListener {

    /** Event manager that will be used to persist the new events resulting from actions. */
    private final EventManager eventManager;

    /** Constructor. */
    public DebugActionListener(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        // intentionally left empty
    }

    @Override
    public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (anAction instanceof StepIntoAction || anAction instanceof ForceStepIntoAction) {
            eventManager.addEvent(new DebugEventBase(EventType.STEP_INTO, new Date()));
        } else if (anAction instanceof StepOverAction || anAction instanceof ForceStepOverAction) {
            eventManager.addEvent(new DebugEventBase(EventType.STEP_OVER, new Date()));
        } else if (anAction instanceof StepOutAction) {
            eventManager.addEvent(new DebugEventBase(EventType.STEP_OUT, new Date()));
        } else if (anAction instanceof XInspectAction) {
            eventManager.addEvent(new DebugEventBase(EventType.INSPECT_VARIABLE, new Date()));
        } else if (anAction.getClass().getSimpleName().equals("XAddToWatchesAction")) {
            eventManager.addEvent(new DebugEventBase(EventType.DEFINE_WATCH, new Date()));
        } else if (anAction.getClass().getSimpleName().equals("EvaluateAction")) {
            eventManager.addEvent(new DebugEventBase(EventType.EVALUATE_EXPRESSION, new Date()));
        } else if (anAction instanceof XSetValueAction) {
            eventManager.addEvent(new DebugEventBase(EventType.MODIFY_VARIABLE_VALUE, new Date()));
        }
    }

    @Override
    public void beforeEditorTyping(char c, DataContext dataContext) {
        // intentionally left empty
    }
}
