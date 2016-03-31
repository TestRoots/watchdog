package nl.tudelft.watchdog.eclipse.logic.event.listeners;

import java.util.Date;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IThread;

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

	/** Constructor. */
	public DebugEventListener(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			if (event.getSource() instanceof IThread) {
				handleDebugEvent(event);
			}
		}
	}

	/**
	 * Creates the correct {@link DebugEventBase} instance based on the input
	 * event's properties.
	 */
	private void handleDebugEvent(DebugEvent event) {
		if (event.getKind() == DebugEvent.SUSPEND) {
			switch (event.getDetail()) {
			case DebugEvent.BREAKPOINT:
				eventManager.addEvent(new DebugEventBase(
						EventType.SUSPEND_BREAKPOINT, new Date()));
				break;
			case DebugEvent.CLIENT_REQUEST:
				eventManager.addEvent(new DebugEventBase(
						EventType.SUSPEND_CLIENT, new Date()));
				break;
			}
		} else if (event.getKind() == DebugEvent.RESUME) {
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
				eventManager.addEvent(new DebugEventBase(
						EventType.RESUME_CLIENT, new Date()));
				break;
			}
		}
	}

}
