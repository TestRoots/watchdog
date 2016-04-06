package nl.tudelft.watchdog.eclipse.logic.event.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpression;

import nl.tudelft.watchdog.core.logic.event.DebugEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventType;

/**
 * Handles all {@link DebugEvent}s and generates the appropriate instances of
 * {@link DebugEventBase} which are then passed to the {@link DebugEventManager}.
 */
public class DebugEventListener implements IDebugEventSetListener {

	/**
	 * The {@link DebugEventManager} used for persisting and transferring the debug
	 * events.
	 */
	private final DebugEventManager debugEventManager;

	/**
	 * List with the hashes of all current watch expressions. Used to avoid
	 * duplicate 'Define Watch' events.
	 */
	private List<Integer> watchExpressionHashes;

	/** Constructor. */
	public DebugEventListener(DebugEventManager debugEventManager) {
		this.debugEventManager = debugEventManager;
		this.watchExpressionHashes = new ArrayList<>();
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			if (isThread(event.getSource())) {
				handleDebugEvent(event);
			} else if (isWatchExpression(event.getSource())) {
				handleWatchExpressionEvent(event);
			} else if (isVariableModificationEvent(event)) {
				debugEventManager.addEvent(new DebugEventBase(
						DebugEventType.MODIFY_VARIABLE_VALUE, new Date()));
			}
		}
	}

	private static boolean isThread(Object source) {
		return source instanceof IThread;
	}

	private static boolean isWatchExpression(Object source) {
		return source instanceof IWatchExpression;
	}

	private static boolean isVariableModificationEvent(DebugEvent event) {
		return event.getSource() instanceof IVariable
				&& event.getKind() == DebugEvent.CHANGE
				&& event.getDetail() == DebugEvent.CONTENT;
	}

	/**
	 * Handles {@link DebugEvent.SUSPEND} and {@link DebugEvent.RESUME} events
	 * based on the input event's properties.
	 */
	private void handleDebugEvent(DebugEvent event) {
		if (event.getKind() == DebugEvent.SUSPEND) {
			handleSuspendEvent(event);
		} else if (event.getKind() == DebugEvent.RESUME) {
			handleResumeEvent(event);
		}
	}

	private void handleSuspendEvent(DebugEvent event) {
		switch (event.getDetail()) {
		case DebugEvent.BREAKPOINT:
			debugEventManager.addEvent(new DebugEventBase(
					DebugEventType.SUSPEND_BREAKPOINT, new Date()));
			break;
		case DebugEvent.CLIENT_REQUEST:
			debugEventManager.addEvent(new DebugEventBase(
					DebugEventType.SUSPEND_CLIENT, new Date()));
			break;
		case DebugEvent.EVALUATION:
			debugEventManager.addEvent(new DebugEventBase(
					DebugEventType.INSPECT_VARIABLE, new Date()));
			break;
		}
	}

	private void handleResumeEvent(DebugEvent event) {
		switch (event.getDetail()) {
		case DebugEvent.STEP_INTO:
			debugEventManager.addEvent(
					new DebugEventBase(DebugEventType.STEP_INTO, new Date()));
			break;
		case DebugEvent.STEP_OVER:
			debugEventManager.addEvent(
					new DebugEventBase(DebugEventType.STEP_OVER, new Date()));
			break;
		case DebugEvent.STEP_RETURN:
			debugEventManager.addEvent(
					new DebugEventBase(DebugEventType.STEP_OUT, new Date()));
			break;
		case DebugEvent.CLIENT_REQUEST:
			debugEventManager.addEvent(new DebugEventBase(
					DebugEventType.RESUME_CLIENT, new Date()));
			break;
		}
	}

	/**
	 * Handles all watch expression events. Makes sure that only one 'Define
	 * Watch' event is generated for each expression.
	 */
	private void handleWatchExpressionEvent(DebugEvent event) {
		if (!watchExpressionHashes.contains(event.getSource().hashCode())) {
			debugEventManager.addEvent(new DebugEventBase(
					DebugEventType.DEFINE_WATCH, new Date()));
			watchExpressionHashes.add(event.getSource().hashCode());
		}
	}
}
