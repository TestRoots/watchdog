package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.PerspectiveInterval.Perspective;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;

/** Listener for Perspective Changes by the user. */
public class PerspectiveListener implements IPerspectiveListener {

	PerspectiveListener() {
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
			WatchDogEventType.START_PERSPECTIVE.process(Perspective.DEBUG);
			break;
		case JavaUI.ID_PERSPECTIVE:
			WatchDogEventType.START_PERSPECTIVE.process(Perspective.JAVA);
			break;
		default:
			WatchDogEventType.START_PERSPECTIVE.process(Perspective.OTHER);
		}
	}
}