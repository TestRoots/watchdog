package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.xdebugger.impl.actions.*;
import com.intellij.xdebugger.impl.ui.tree.actions.XInspectAction;
import com.intellij.xdebugger.impl.ui.tree.actions.XSetValueAction;
import nl.tudelft.watchdog.core.logic.event.DebugEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventType;

import java.util.Date;

/**
 * Listener for derivations of {@link AnAction}s related to the debugger. On receipt of these actions, the appropriate events are generated and given to the event manager.
 */
public class DebugActionListener implements AnActionListener {

    /**
     * DebugEventManager that will be used to persist the new events resulting from actions.
     */
    private final DebugEventManager debugEventManager;

    /**
     * Constructor.
     */
    public DebugActionListener(DebugEventManager debugEventManager) {
        this.debugEventManager = debugEventManager;
    }

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        // intentionally left empty
    }

    @Override
    public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (anAction instanceof StepIntoAction || anAction instanceof ForceStepIntoAction) {
            createEvent(DebugEventType.STEP_INTO);
        } else if (anAction instanceof StepOverAction || anAction instanceof ForceStepOverAction) {
            createEvent(DebugEventType.STEP_OVER);
        } else if (anAction instanceof StepOutAction) {
            createEvent(DebugEventType.STEP_OUT);
        } else if (anAction instanceof XInspectAction) {
            createEvent(DebugEventType.INSPECT_VARIABLE);
        } else if (anAction.getClass().getSimpleName().equals("XAddToWatchesAction")) {
            createEvent(DebugEventType.DEFINE_WATCH);
        } else if (anAction.getClass().getSimpleName().equals("EvaluateAction")) {
            createEvent(DebugEventType.EVALUATE_EXPRESSION);
        } else if (anAction instanceof XSetValueAction) {
            createEvent(DebugEventType.MODIFY_VARIABLE_VALUE);
        }
    }

    private void createEvent(DebugEventType debugEventType) {
        debugEventManager.addEvent(new DebugEventBase(debugEventType,new Date()));
    }

    @Override
    public void beforeEditorTyping(char c, DataContext dataContext) {
        // intentionally left empty
    }
}
