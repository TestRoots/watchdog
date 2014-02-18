package nl.tudelft.watchdog.eclipseUIReader.Events;

public class DocumentNotifier {
	// Create the listener list
    static protected javax.swing.event.EventListenerList listenerList =
        new javax.swing.event.EventListenerList();

    // This methods allows classes to register for MyEvents
    public static void addMyEventListener(IDocumentAttentionListener listener) {
        listenerList.add(IDocumentAttentionListener.class, listener);
    }

    // This methods allows classes to unregister for MyEvents
    public static void removeMyEventListener(IDocumentAttentionListener listener) {
        listenerList.remove(IDocumentAttentionListener.class, listener);
    }

    public static void fireDocumentStartEditingEvent(DocumentActivateEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==IDocumentAttentionListener.class) {
                ((IDocumentAttentionListener)listeners[i+1]).onDocumentStartEditing(evt);
            }
        }
    }
    public static void fireDocumentStopEditingEvent(DocumentDeActivateEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==IDocumentAttentionListener.class) {
                ((IDocumentAttentionListener)listeners[i+1]).onDocumentStopEditing(evt);
            }
        }
    }
    public static void fireDocumentStartFocusEvent(DocumentActivateEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==IDocumentAttentionListener.class) {
                ((IDocumentAttentionListener)listeners[i+1]).onDocumentStartFocus(evt);
            }
        }
    }
    public static void fireDocumentEndFocusEvent(DocumentDeActivateEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==IDocumentAttentionListener.class) {
                ((IDocumentAttentionListener)listeners[i+1]).onDocumentEndFocus(evt);
            }
        }
    }
}
