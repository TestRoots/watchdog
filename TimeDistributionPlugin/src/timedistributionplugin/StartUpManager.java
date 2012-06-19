package timeDistributionPlugin;

import org.eclipse.ui.IStartup;

import eclipseUIReader.UIListener;
import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;
import eclipseUIReader.Events.IDocumentAttentionListener;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {
		MyLogger.logInfo("Plugin startup");
		new UIListener().attachListeners();
		
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {
			
			@Override
			public void onDocumentAlert(DocumentAttentionEvent evt) {
				System.out.println(evt.getText());
			}
		});
	}

}
