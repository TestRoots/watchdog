package eclipseUIReader.Events;

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

    protected static void fireDocumentActivatedEvent(DocumentAttentionEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==IDocumentAttentionListener.class) {
                ((IDocumentAttentionListener)listeners[i+1]).onDocumentActivated(evt);
            }
        }
    }
    protected static void fireDocumentDeactivatedEvent(DocumentAttentionEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==IDocumentAttentionListener.class) {
                ((IDocumentAttentionListener)listeners[i+1]).onDocumentDeactivated(evt);
            }
        }
    }
}
