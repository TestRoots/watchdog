package nl.tudelft.watchdog.eclipse.logic.event.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpression;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

/**
 * Handles all {@link DebugEvent}s and generates the appropriate instances of
 * {@link DebugEventBase} which are then passed to the {@link TrackingEventManager}.
 */
public class DebugEventListener implements IDebugEventSetListener {

	/**
	 * The {@link TrackingEventManager} used for persisting and transferring the debug
	 * events.
	 */
	private final TrackingEventManager TrackingEventManager;

	/**
	 * List with the hashes of all current watch expressions. Used to avoid
	 * duplicate 'Define Watch' events.
	 */
	private List<Integer> watchExpressionHashes;

	/** Constructor. */
	public DebugEventListener(TrackingEventManager TrackingEventManager) {
		this.TrackingEventManager = TrackingEventManager;
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
				TrackingEventManager.addEvent(new DebugEventBase(
						TrackingEventType.MODIFY_VARIABLE_VALUE, new Date()));
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
			TrackingEventManager.addEvent(new DebugEventBase(
					TrackingEventType.SUSPEND_BREAKPOINT, new Date()));
			break;
		case DebugEvent.CLIENT_REQUEST:
			TrackingEventManager.addEvent(new DebugEventBase(
					TrackingEventType.SUSPEND_CLIENT, new Date()));
			break;
		case DebugEvent.EVALUATION:
			TrackingEventManager.addEvent(new DebugEventBase(
					TrackingEventType.INSPECT_VARIABLE, new Date()));
			break;
		}
	}

	private void handleResumeEvent(DebugEvent event) {
		switch (event.getDetail()) {
		case DebugEvent.STEP_INTO:
			TrackingEventManager.addEvent(
					new DebugEventBase(TrackingEventType.STEP_INTO, new Date()));
			break;
		case DebugEvent.STEP_OVER:
			TrackingEventManager.addEvent(
					new DebugEventBase(TrackingEventType.STEP_OVER, new Date()));
			break;
		case DebugEvent.STEP_RETURN:
			TrackingEventManager.addEvent(
					new DebugEventBase(TrackingEventType.STEP_OUT, new Date()));
			break;
		case DebugEvent.CLIENT_REQUEST:
			TrackingEventManager.addEvent(new DebugEventBase(
					TrackingEventType.RESUME_CLIENT, new Date()));
			break;
		}
	}

	/**
	 * Handles all watch expression events. Makes sure that only one 'Define
	 * Watch' event is generated for each expression.
	 */
	private void handleWatchExpressionEvent(DebugEvent event) {
		if (!watchExpressionHashes.contains(event.getSource().hashCode())) {
			TrackingEventManager.addEvent(new DebugEventBase(
					TrackingEventType.DEFINE_WATCH, new Date()));
			watchExpressionHashes.add(event.getSource().hashCode());
		}
	}
}
