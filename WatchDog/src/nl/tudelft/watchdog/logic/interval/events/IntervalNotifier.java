package nl.tudelft.watchdog.logic.interval.events;

public class IntervalNotifier {
	// Create the listener list
	static protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	// This methods allows classes to register for MyEvents
	protected static void addMyEventListener(IIntervalListener listener) {
		listenerList.add(IIntervalListener.class, listener);
	}

	// This methods allows classes to unregister for MyEvents
	protected static void removeMyEventListener(IIntervalListener listener) {
		listenerList.remove(IIntervalListener.class, listener);
	}

	protected static void fireOnNewInterval(NewIntervalEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == IIntervalListener.class) {
				((IIntervalListener) listeners[i + 1]).onNewInterval(evt);
			}
		}
	}

	protected static void fireOnClosingInterval(ClosingIntervalEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == IIntervalListener.class) {
				((IIntervalListener) listeners[i + 1]).onClosingInterval(evt);
			}
		}
	}
}
