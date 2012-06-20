package timeDistributionPlugin;

import interval.ActiveInterval;

import org.eclipse.ui.IStartup;


import eclipseUIReader.UIListener;
import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;
import eclipseUIReader.Events.IDocumentAttentionListener;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {
		MyLogger.logInfo("Plugin startup");
		
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {
			
			@Override
			public void onDocumentActivated(DocumentAttentionEvent evt) {
				System.out.println(evt.getChangedEditor().getTitle() + " is activated");
				
				ActiveInterval activeInterval = new ActiveInterval(evt.getChangedEditor(), 3000);
			}

			@Override
			public void onDocumentDeactivated(DocumentAttentionEvent evt) {
				System.out.println(evt.getChangedEditor().getTitle() + " is DEactivated");
			}
		});
		
		new UIListener().attachListeners();
		
	}

}
