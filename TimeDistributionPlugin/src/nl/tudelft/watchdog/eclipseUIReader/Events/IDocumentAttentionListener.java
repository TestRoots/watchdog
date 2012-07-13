package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventListener;

public interface IDocumentAttentionListener extends EventListener  {
	public void onDocumentStartEditing(DocumentAttentionEvent evt);
	public void onDocumentStopEditing(DocumentAttentionEvent evt);
	
	public void onDocumentStartFocus(DocumentAttentionEvent evt);
	public void onDocumentEndFocus(DocumentAttentionEvent evt);
}
