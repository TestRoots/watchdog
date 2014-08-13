package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent.EventType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A listener that determines whether there was general activity in the Eclipse
 * window.
 */
public class GeneralActivityListener {

	/** Constructor. */
	public GeneralActivityListener(final EventManager eventManager,
			Display display) {
		display.addFilter(SWT.KeyDown | SWT.KeyUp | SWT.MouseDown | SWT.MouseUp
				| SWT.MouseMove, new Listener() {

			@Override
			public void handleEvent(Event event) {
				eventManager
						.update(new WatchDogEvent(event, EventType.ACTIVITY));
			}
		});
	}
}
