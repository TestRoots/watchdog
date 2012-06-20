package interval;


import timeDistributionPlugin.MyLogger;
import interval.ChangerCheckerTask.RunCallBack;
import eclipseUIReader.UIListener;
import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;
import eclipseUIReader.Events.IDocumentAttentionListener;

public class IntervalManager {
	private ActiveInterval currentInterval;
	
	public IntervalManager(){
		
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {			
			
			@Override
			public void onDocumentActivated(final DocumentAttentionEvent evt) {
				System.out.println("active doc");
				//create a new active interval when doc is new
				if(currentInterval == null || currentInterval.getEditor() != evt.getChangedEditor()){
					ActiveInterval activeInterval = new ActiveInterval(evt.getChangedEditor());
					currentInterval = activeInterval;
					activeInterval.start(3000, new RunCallBack() {
						
						@Override
						public void onInactive() {
							//finish this interval, do something with it
							System.out.println("interval finished for "+ evt.getChangedEditor().getTitle());
							currentInterval = null;
						}
					});
					System.out.println("interval created for "+evt.getChangedEditor().getTitle());
				}
			}
			@Override
			public void onDocumentDeactivated(DocumentAttentionEvent evt) {
				System.out.println("deactive doc");
				//figure out the editor that belongs to this deactivation, and close his interval
				if(currentInterval == null)
					MyLogger.logInfo("Document was deactivated that wasnt being tracked, probably an inactive document");
				else if(evt.getChangedEditor() == currentInterval.getEditor()){
					currentInterval.getTimer().cancel(); //stop the timer that checks for changes in a doc
					currentInterval = null;
					System.out.println("interval finished(2) for"+ evt.getChangedEditor().getTitle());					
				}
				else
					MyLogger.logSevere("Some other Document ("+evt.getChangedEditor().getTitle()+") was deactivated that wasnt being tracked("+currentInterval.getEditor().getTitle()+")!");
			}
			
		});
		
		new UIListener().attachListeners();
		
	}
}
