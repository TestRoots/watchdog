package nl.tudelft.watchdog.logic.eclipseuireader.events;

import java.util.EventListener;

public interface IDocumentAttentionListener extends EventListener {
	public void onDocumentStartEditing(DocumentActivateOrDeactivateEvent evt);

	public void onDocumentStopEditing(DocumentActivateOrDeactivateEvent evt);

	public void onDocumentStartFocus(DocumentActivateOrDeactivateEvent evt);

	public void onDocumentEndFocus(DocumentActivateOrDeactivateEvent evt);
}
