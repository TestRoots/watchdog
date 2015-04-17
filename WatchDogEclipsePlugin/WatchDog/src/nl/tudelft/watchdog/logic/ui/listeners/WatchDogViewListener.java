package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.ui.WatchDogView;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * Listener for {@link WatchDogView}. Catches when the view is actually visible
 * by the user and not hidden by other parts.
 **/
public class WatchDogViewListener implements IPartListener2 {
	/** Constructor. */
	public WatchDogViewListener(WatchDogView watchdogView) {
		this.watchDogView = watchdogView;
	}

	private WatchDogView watchDogView;

	/** The Event Manager. */
	private EventManager eventManager;

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
		// eventManager.update(new WatchDogEvent(this,
		// EventType.START_WATCHDOGVIEW));
		System.out.println(isVisible());
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// eventManager
		// .update(new WatchDogEvent(this, EventType.END_WATCHDOGVIEW));
		System.out.println(isVisible());
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// intentionally empty
	}

	private boolean isVisible() {
		return watchDogView.getSite().getPage().isPartVisible(watchDogView);
	}

}
