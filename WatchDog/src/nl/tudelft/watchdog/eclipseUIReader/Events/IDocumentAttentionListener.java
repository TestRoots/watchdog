package nl.tudelft.watchdog.eclipseUIReader.Events;

import java.util.EventListener;

public interface IDocumentAttentionListener extends EventListener  {
	public void onDocumentStartEditing(DocumentActivateEvent evt);
	public void onDocumentStopEditing(DocumentDeActivateEvent evt);
	
	public void onDocumentStartFocus(DocumentActivateEvent evt);
	public void onDocumentEndFocus(DocumentDeActivateEvent evt);
}
