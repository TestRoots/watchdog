package nl.tudelft.watchdog.logic.eclipseuireader.events;

import java.util.EventListener;

public interface IDocumentAttentionListener extends EventListener {
	public void onDocumentStartEditing(EditorEvent evt);

	public void onDocumentStopEditing(EditorEvent evt);

	public void onDocumentStartFocus(EditorEvent evt);

	public void onDocumentEndFocus(EditorEvent evt);
}
