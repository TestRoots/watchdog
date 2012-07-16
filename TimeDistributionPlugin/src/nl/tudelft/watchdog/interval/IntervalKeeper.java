package nl.tudelft.watchdog.interval;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nl.tudelft.watchdog.document.DocumentFactory;
import nl.tudelft.watchdog.document.IDocument;
import nl.tudelft.watchdog.eclipseUIReader.IUIListener;
import nl.tudelft.watchdog.eclipseUIReader.UIListener;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentAttentionEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentNotifier;
import nl.tudelft.watchdog.eclipseUIReader.Events.IDocumentAttentionListener;
import nl.tudelft.watchdog.interval.active.ActiveEditingInterval;
import nl.tudelft.watchdog.interval.active.ActiveInterval;
import nl.tudelft.watchdog.interval.active.ActiveReadingInterval;
import nl.tudelft.watchdog.interval.activityCheckers.RunCallBack;
import nl.tudelft.watchdog.interval.events.ClosingIntervalEvent;
import nl.tudelft.watchdog.interval.events.IIntervalListener;
import nl.tudelft.watchdog.interval.events.IntervalNotifier;
import nl.tudelft.watchdog.interval.events.NewIntervalEvent;
import nl.tudelft.watchdog.interval.recorded.ActivityType;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MessageConsoleManager;


public class IntervalKeeper extends IntervalNotifier implements IIntervalKeeper  {
	//TODO: in settings file?
	private final long TIMEOUT;
	
	private ActiveReadingInterval currentReadingInterval;
	private ActiveEditingInterval currentEditingInterval;
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
		
		listenToDocumentChanges();
		UIListener = new UIListener();
		UIListener.attachListeners();		
	}

	private void listenToDocumentChanges() {
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {			
			
			@Override
			public void onDocumentStartEditing(final DocumentAttentionEvent evt) {
				//create a new active interval when doc is new
				if(currentEditingInterval == null || currentEditingInterval.isClosed()){
					createNewEditingInterval(evt);	
				}
				else if(currentEditingInterval.getEditor() != evt.getChangedEditor()){
					closeCurrentInterval(currentEditingInterval);
					createNewEditingInterval(evt);
				}
			}		
			
			@Override
			public void onDocumentStopEditing(DocumentAttentionEvent evt) {
				if(currentEditingInterval != null && evt.getChangedEditor() == currentEditingInterval.getEditor()){										
					closeCurrentInterval(currentEditingInterval);
				}				
			}


			@Override
			public void onDocumentStartFocus(DocumentAttentionEvent evt) {
				MessageConsoleManager.getConsoleStream().println("onDocumentStartFocus" + evt.getChangedEditor().getTitle());
				//create a new active interval when doc is new
				if(currentReadingInterval == null || currentReadingInterval.isClosed()){
					createNewReadingInterval(evt);
				}
				else if(currentReadingInterval.getEditor() != evt.getChangedEditor()){
					closeCurrentInterval(currentEditingInterval);
					createNewReadingInterval(evt);
				}
			}


			@Override
			public void onDocumentEndFocus(DocumentAttentionEvent evt) {
				MessageConsoleManager.getConsoleStream().println("onDocumentEndFocus" + evt.getChangedEditor().getTitle());
				if(currentReadingInterval != null && evt.getChangedEditor() == currentReadingInterval.getEditor()){										
					closeCurrentInterval(currentReadingInterval);
				}
			}
			
		});
	}
	
	private void closeCurrentInterval(ActiveInterval interval) {	
		IDocument doc = DocumentFactory.createDocument(interval.getEditor());
		RecordedInterval recordedInterval = new RecordedInterval(doc, interval.getTimeOfCreation(), new Date(), intervalToActivityType(interval));
		recordedIntervals.add(recordedInterval);
		interval.closeInterval();
		IntervalNotifier.fireOnClosingInterval(new ClosingIntervalEvent(recordedInterval));
	}
	
	private void createNewEditingInterval(final DocumentAttentionEvent evt) {				
		ActiveEditingInterval activeInterval = new ActiveEditingInterval(evt.getChangedEditor());
		currentEditingInterval = activeInterval;
		activeInterval.addTimeoutListener(TIMEOUT, new RunCallBack() {					
			@Override
			public void onInactive() {
				closeCurrentInterval(currentEditingInterval);
			}
		});
		IntervalNotifier.fireOnNewInterval(new NewIntervalEvent(activeInterval));
	}
	private void createNewReadingInterval(final DocumentAttentionEvent evt) {				
		ActiveReadingInterval activeInterval = new ActiveReadingInterval(evt.getChangedEditor());
		currentReadingInterval = activeInterval;
		
		activeInterval.addTimeoutListener(TIMEOUT, new RunCallBack() {					
			@Override
			public void onInactive() {
				closeCurrentInterval(currentEditingInterval);
			}
		});
		IntervalNotifier.fireOnNewInterval(new NewIntervalEvent(activeInterval));
		
	}
	
	//TODO: niet tevreden met deze oplossing. alex vragen?
	private ActivityType intervalToActivityType(ActiveInterval interval){
		if(interval instanceof ActiveEditingInterval){
			return ActivityType.Editing;
		}
		else if(interval instanceof ActiveReadingInterval){
			return ActivityType.Reading;
		}
		else
			return ActivityType.Unknown;
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
	
	@Override
	public void setRecordedIntervals(List<IInterval> intervals){
		recordedIntervals = intervals;
	}
}
