package nl.tudelft.watchdog.interval;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nl.tudelft.watchdog.document.Document;
import nl.tudelft.watchdog.document.DocumentType;
import nl.tudelft.watchdog.document.IDocument;
import nl.tudelft.watchdog.eclipseUIReader.IUIListener;
import nl.tudelft.watchdog.eclipseUIReader.UIListener;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentAttentionEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentNotifier;
import nl.tudelft.watchdog.eclipseUIReader.Events.IDocumentAttentionListener;
import nl.tudelft.watchdog.interval.activityCheckers.RunCallBack;
import nl.tudelft.watchdog.interval.events.ClosingIntervalEvent;
import nl.tudelft.watchdog.interval.events.IIntervalListener;
import nl.tudelft.watchdog.interval.events.IntervalNotifier;
import nl.tudelft.watchdog.interval.events.NewIntervalEvent;


public class IntervalKeeper extends IntervalNotifier implements IIntervalKeeper  {
	//TODO: in settings file?
	private final long TIMEOUT;
	
	private ActiveInterval currentInterval;
	private IUIListener UIListener;
	
	private List<IInterval> recordedIntervals;
	
	private static IntervalKeeper instance = null;
	
	public static IntervalKeeper getInstance(){
		if(instance == null)
			instance = new IntervalKeeper();
		return instance;
	}
	
	private IntervalKeeper(){
		TIMEOUT = 3000;
		
		recordedIntervals= new LinkedList<IInterval>();
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
		IDocument doc = new Document(currentInterval.getEditor().getTitle(), DocumentType.PRODUCTION);
		RecordedInterval recordedInterval = new RecordedInterval(doc, currentInterval.getTimeOfCreation(), new Date());
		recordedIntervals.add(recordedInterval);
		currentInterval.getTimer().cancel(); //stop the timer that checks for changes in a doc
		currentInterval = null;
		IntervalNotifier.fireOnClosingInterval(new ClosingIntervalEvent(recordedInterval));
	}
	
	private void createNewInterval(final DocumentAttentionEvent evt) {				
		ActiveInterval activeInterval = new ActiveInterval(evt.getChangedEditor());
		currentInterval = activeInterval;
		activeInterval.start(TIMEOUT, new RunCallBack() {					
			@Override
			public void onInactive() {
				closeCurrentInterval();
			}
		});
		IntervalNotifier.fireOnNewInterval(new NewIntervalEvent(activeInterval));
	}
	
	@Override
	public void addIntervalListener(IIntervalListener listener){
		IntervalNotifier.addMyEventListener(listener);
	}
	
	@Override
	public void removeIntervalListener(IIntervalListener listener){
		IntervalNotifier.removeMyEventListener(listener);
	}
	
	@Override
	public List<IInterval> getRecordedIntervals(){
		return recordedIntervals;
	}
}
