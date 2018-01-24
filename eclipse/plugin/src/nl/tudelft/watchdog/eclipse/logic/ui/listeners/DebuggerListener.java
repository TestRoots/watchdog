package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;

/** Listener for events fired by the debugger. */
public class DebuggerListener implements IDebugEventSetListener {

	/** Constructor. */
	public DebuggerListener() {
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			switch (event.getKind()) {
			case DebugEvent.CREATE:
				if (event.getSource() instanceof IDebugTarget) {
					new WatchDogEvent(this, EventType.START_DEBUG).update();
				}
				break;

			case DebugEvent.TERMINATE:
				if (event.getSource() instanceof IDebugTarget) {
					new WatchDogEvent(this, EventType.END_DEBUG).update();
				}
				break;

			default:
				break;
			}
		}
	}

}
