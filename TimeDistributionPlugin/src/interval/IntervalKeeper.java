package interval;


import interval.activityCheckers.ChangerCheckerTask.RunCallBack;
import interval.events.IIntervalListener;
import interval.events.IntervalEvent;
import interval.events.IntervalNotifier;
import eclipseUIReader.IUIListener;
import eclipseUIReader.UIListener;
import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;
import eclipseUIReader.Events.IDocumentAttentionListener;

public class IntervalKeeper extends IntervalNotifier implements IIntervalKeeper  {
	private ActiveInterval currentInterval;
	private IUIListener UIListener;
	
	public IntervalKeeper(){
		
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {			
			
			@Override
			public void onDocumentActivated(final DocumentAttentionEvent evt) {
				if(currentInterval != null && currentInterval.getEditor() != evt.getChangedEditor()){
					closeCurrentInterval();					
				}
				
				//create a new active interval when doc is new
				if(currentInterval == null){
					createNewInterval(evt);	
				}
			}
						
			
			@Override
			public void onDocumentDeactivated(DocumentAttentionEvent evt) {
				if(currentInterval != null && evt.getChangedEditor() == currentInterval.getEditor()){										
					closeCurrentInterval();
				}				
			}
			
		});
		UIListener = new UIListener();
		UIListener.attachListeners();		
	}
	
	private void closeCurrentInterval() {				
		currentInterval.getTimer().cancel(); //stop the timer that checks for changes in a doc
		currentInterval = null;
		//IntervalNotifier.fireOnClosingInterval(new IntervalEvent(new Interval(null)));
	}
	
	private void createNewInterval(final DocumentAttentionEvent evt) {				
		ActiveInterval activeInterval = new ActiveInterval(evt.getChangedEditor());
		currentInterval = activeInterval;
		activeInterval.start(3000, new RunCallBack() {					
			@Override
			public void onInactive() {
				closeCurrentInterval();
			}
		});
		IntervalNotifier.fireOnNewInterval(new IntervalEvent(activeInterval));
	}
	
	public void addIntervalListener(IIntervalListener listener){
		IntervalNotifier.addMyEventListener(listener);
	}
	public void removeIntervalListener(IIntervalListener listener){
		IntervalNotifier.removeMyEventListener(listener);
	}
}
