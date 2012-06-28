package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventListener;

public interface IDocumentAttentionListener extends EventListener  {
	public void onDocumentActivated(DocumentAttentionEvent evt);
	public void onDocumentDeactivated(DocumentAttentionEvent evt);
}
