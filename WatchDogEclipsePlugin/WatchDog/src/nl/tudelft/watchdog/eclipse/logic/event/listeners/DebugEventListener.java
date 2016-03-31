package nl.tudelft.watchdog.eclipse.logic.event.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpression;

import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventType;

/**
 * Class that handles all {@link DebugEvent}s and generates the appropriate
 * instances of {@link DebugEventBase} which are then passed to the
 * {@link EventManager}.
 */
public class DebugEventListener implements IDebugEventSetListener {

	/**
	 * The {@link EventManager} used for persisting and transferring the debug
	 * events.
	 */
	private final EventManager eventManager;

	/**
	 * List with the hashes of all current watch expressions. Used to avoid
	 * duplicate 'Define Watch' events.
	 */
	private List<Integer> watchExpressionHashes;

	/** Constructor. */
	public DebugEventListener(EventManager eventManager) {
		this.eventManager = eventManager;
		this.watchExpressionHashes = new ArrayList<>();
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			if (event.getSource() instanceof IThread) {
				handleDebugEvent(event);
			} else if (event.getSource() instanceof IWatchExpression) {
				handleWatchExpressionEvent(event);
			} else if (event.getSource() instanceof IVariable
					&& event.getKind() == DebugEvent.CHANGE
					&& event.getDetail() == DebugEvent.CONTENT) {
				eventManager.addEvent(new DebugEventBase(
						EventType.MODIFY_VARIABLE_VALUE, new Date()));
			}
		}
	}

	/**
	 * Creates the correct {@link DebugEventBase} instance for SUSPEND and
	 * RESUME events based on the input event's properties.
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
			eventManager.addEvent(new DebugEventBase(
					EventType.SUSPEND_BREAKPOINT, new Date()));
			break;
		case DebugEvent.CLIENT_REQUEST:
			eventManager.addEvent(
					new DebugEventBase(EventType.SUSPEND_CLIENT, new Date()));
			break;
		case DebugEvent.EVALUATION:
			eventManager.addEvent(
					new DebugEventBase(EventType.INSPECT_VARIABLE, new Date()));
			break;
		}
	}

	private void handleResumeEvent(DebugEvent event) {
		switch (event.getDetail()) {
		case DebugEvent.STEP_INTO:
			eventManager.addEvent(
					new DebugEventBase(EventType.STEP_INTO, new Date()));
			break;
		case DebugEvent.STEP_OVER:
			eventManager.addEvent(
					new DebugEventBase(EventType.STEP_OVER, new Date()));
			break;
		case DebugEvent.STEP_RETURN:
			eventManager.addEvent(
					new DebugEventBase(EventType.STEP_OUT, new Date()));
			break;
		case DebugEvent.CLIENT_REQUEST:
			eventManager.addEvent(
					new DebugEventBase(EventType.RESUME_CLIENT, new Date()));
			break;
		}
	}

	/**
	 * Handles all watch expression events. Makes sure that only one 'Define
	 * Watch' event is generated for each expression.
	 */
	private void handleWatchExpressionEvent(DebugEvent event) {
		if (!watchExpressionHashes.contains(event.getSource().hashCode())) {
			eventManager.addEvent(
					new DebugEventBase(EventType.DEFINE_WATCH, new Date()));
			watchExpressionHashes.add(event.getSource().hashCode());
		}
	}
}
