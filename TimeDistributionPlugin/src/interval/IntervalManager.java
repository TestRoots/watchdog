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
				if(currentInterval != null && currentInterval.getEditor() != evt.getChangedEditor()){
					//clean up old one first
					currentInterval.getTimer().cancel(); //stop the timer that checks for changes in a doc
					System.out.println("interval finished by cleanup(3) for"+ currentInterval.getEditor().getTitle());
					currentInterval = null;					
				}
				
				//create a new active interval when doc is new
				if(currentInterval == null){
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
				//figure out the editor that belongs to this deactivation, and close his interval
				if(currentInterval == null)
					MyLogger.logInfo("Document was deactivated that wasnt being tracked, probably an inactive document");
				else if(evt.getChangedEditor() == currentInterval.getEditor()){
					currentInterval.getTimer().cancel(); //stop the timer that checks for changes in a doc
					currentInterval = null;
					System.out.println("interval finished(2) for"+ evt.getChangedEditor().getTitle());					
				}
				else
					MyLogger.logInfo("Some other Document ("+evt.getChangedEditor().getTitle()+") was deactivated that was already cleaned up("+currentInterval.getEditor().getTitle()+")!");
			}
			
		});
		
		new UIListener().attachListeners();
		
	}
}
