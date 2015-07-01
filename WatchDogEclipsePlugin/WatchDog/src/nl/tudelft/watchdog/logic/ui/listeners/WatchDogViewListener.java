package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.ui.WatchDogView;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * Listener for {@link WatchDogView}. Catches when the view is actually visible
 * by the user and not hidden by other parts.
 **/
public class WatchDogViewListener implements IPartListener2 {

	private WatchDogView watchDogView;

	/** The Event Manager. */
	private final EventManager eventManager;

	/** Constructor. */
	public WatchDogViewListener(WatchDogView watchDogView) {
		this.watchDogView = watchDogView;
		this.eventManager = InitializationManager.getInstance()
				.getEventManager();
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
			eventManager.update(new WatchDogEvent(this,
					EventType.START_WATCHDOGVIEW));
		} else {
			eventManager.update(new WatchDogEvent(this,
					EventType.END_WATCHDOGVIEW));

		}
	}

	private boolean isVisible() {
		return watchDogView.getSite().getPage().isPartVisible(watchDogView);
	}

}
