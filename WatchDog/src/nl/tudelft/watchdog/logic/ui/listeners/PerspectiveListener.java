package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent.EventType;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;

/** Listener for Perspective Changes by the user. */
public class PerspectiveListener implements IPerspectiveListener {
	/** The eventObservable. */
	private EventManager eventManager;

	/** Constructor. */
	PerspectiveListener(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective, String changeId) {
		// intentionally left empty
	}

	@Override
	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {
		switch (perspective.getId()) {
		case IDebugUIConstants.ID_DEBUG_PERSPECTIVE:
			eventManager.update(new WatchDogEvent(perspective,
					EventType.START_DEBUG_PERSPECTIVE));
			break;
		case JavaUI.ID_PERSPECTIVE:
			eventManager.update(new WatchDogEvent(perspective,
					EventType.START_JAVA_PERSPECTIVE));
			break;
		default:
			eventManager.update(new WatchDogEvent(perspective.getId(),
					EventType.START_UNKNOWN_PERSPECTIVE));
		}
	}
}