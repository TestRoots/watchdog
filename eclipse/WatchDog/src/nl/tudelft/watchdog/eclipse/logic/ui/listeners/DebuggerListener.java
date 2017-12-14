package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.eclipse.logic.ui.WatchDogEventManager;

/** Listener for events fired by the debugger. */
public class DebuggerListener implements IDebugEventSetListener {

	private final WatchDogEventManager eventManager;

	/** Constructor. */
	public DebuggerListener(WatchDogEventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			switch (event.getKind()) {
			case DebugEvent.CREATE:
				if (event.getSource() instanceof IDebugTarget) {
					eventManager.update(
							new WatchDogEvent(this, EventType.START_DEBUG));
				}
				break;

			case DebugEvent.TERMINATE:
				if (event.getSource() instanceof IDebugTarget) {
					eventManager.update(
							new WatchDogEvent(this, EventType.END_DEBUG));
				}
				break;

			default:
				break;
			}
		}
	}

}
