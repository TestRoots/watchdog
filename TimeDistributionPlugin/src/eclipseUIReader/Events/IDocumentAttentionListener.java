package eclipseUIReader.Events;

import java.util.EventListener;

public interface IDocumentAttentionListener extends EventListener  {
	public void onDocumentAlert(DocumentAttentionEvent evt);
}
