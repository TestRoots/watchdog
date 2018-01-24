package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.eclipse.ui.WatchDogView;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * Listener for {@link WatchDogView}. Catches when the view is actually visible
 * by the user and not hidden by other parts.
 **/
public class WatchDogViewListener implements IPartListener2 {

	private WatchDogView watchDogView;

	public WatchDogViewListener(WatchDogView watchDogView) {
		this.watchDogView = watchDogView;
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// intentionally empty
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// intentionally empty
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		// intentionally empty
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// intentionally empty
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		// intentionally empty
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		triggerEventManager();
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		triggerEventManager();
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// intentionally empty
	}

	private void triggerEventManager() {
		if (isVisible()) {
			new WatchDogEvent(this, EventType.START_WATCHDOGVIEW).update();
		} else {
			new WatchDogEvent(this, EventType.END_WATCHDOGVIEW).update();

		}
	}

	private boolean isVisible() {
		return watchDogView.getSite().getPage().isPartVisible(watchDogView);
	}

}
