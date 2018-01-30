package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

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
					WatchDogEventType.START_DEBUG.process(this);
				}
				break;

			case DebugEvent.TERMINATE:
				if (event.getSource() instanceof IDebugTarget) {
					WatchDogEventType.END_DEBUG.process(this);
				}
				break;

			default:
				break;
			}
		}
	}

}
