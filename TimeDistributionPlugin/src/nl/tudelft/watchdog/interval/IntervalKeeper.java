package nl.tudelft.watchdog.interval;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.document.DocumentFactory;
import nl.tudelft.watchdog.document.IDocument;
import nl.tudelft.watchdog.document.IDocumentFactory;
import nl.tudelft.watchdog.eclipseUIReader.IUIListener;
import nl.tudelft.watchdog.eclipseUIReader.UIListener;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentActivateEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentDeActivateEvent;
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
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.plugin.PrefPage;
import nl.tudelft.watchdog.util.WatchDogUtil;


public class IntervalKeeper extends IntervalNotifier implements IIntervalKeeper  {
	
	private ActiveReadingInterval currentReadingInterval;
	private ActiveEditingInterval currentEditingInterval;
	private IUIListener UIListener;
	private IDocumentFactory documentFactory;
	
	private List<IInterval> recordedIntervals;
	
	private static IntervalKeeper instance = null;
	
	public static IntervalKeeper getInstance(){
		if(instance == null)
			instance = new IntervalKeeper();
		return instance;
	}
	
	private IntervalKeeper(){
		recordedIntervals= new ArrayList<IInterval>();
		
		listenToDocumentChanges();
		UIListener = new UIListener();
		UIListener.attachListeners();
		documentFactory = new DocumentFactory();
	}

	private void listenToDocumentChanges() {
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {			
			
			@Override
			public void onDocumentStartEditing(final DocumentActivateEvent evt) {
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
			public void onDocumentStopEditing(DocumentDeActivateEvent evt) {
				if(currentEditingInterval != null && evt.getChangedEditor() == currentEditingInterval.getEditor()){										
					closeCurrentInterval(currentEditingInterval);
				}				
			}


			@Override
			public void onDocumentStartFocus(DocumentActivateEvent evt) {
				//MessageConsoleManager.getConsoleStream().println("onDocumentStartFocus" + evt.getChangedEditor().getTitle());
				//create a new active interval when doc is new
				if(currentReadingInterval == null || currentReadingInterval.isClosed()){
					createNewReadingInterval(evt);
				}
				else if(currentReadingInterval.getEditor() != evt.getChangedEditor()){
					closeCurrentInterval(currentReadingInterval);
					createNewReadingInterval(evt);
				}
			}


			@Override
			public void onDocumentEndFocus(DocumentDeActivateEvent evt) {
				//MessageConsoleManager.getConsoleStream().println("onDocumentEndFocus" + evt.getChangedEditor().getTitle());
				if(currentReadingInterval != null && evt.getChangedEditor() == currentReadingInterval.getEditor()){										
					closeCurrentInterval(currentReadingInterval);
				}
			}
			
		});
	}
	
	private void closeCurrentInterval(ActiveInterval interval) {	
		if(!interval.isClosed()){
			IDocument doc = documentFactory.createDocument(interval.getPart());
			RecordedInterval recordedInterval = new RecordedInterval(doc, interval.getTimeOfCreation(), new Date(), interval.getActivityType(), WatchDogUtil.isInDebugMode());
			recordedIntervals.add(recordedInterval);
			interval.closeInterval();
			IntervalNotifier.fireOnClosingInterval(new ClosingIntervalEvent(recordedInterval));
		}
	}
	
	private void createNewEditingInterval(final DocumentActivateEvent evt) {				
		ActiveEditingInterval activeInterval = new ActiveEditingInterval(evt.getChangedEditor());
		currentEditingInterval = activeInterval;
		addNewIntervalHandlers(activeInterval, PrefPage.getTimeOutEditing());
	}
	private void createNewReadingInterval(final DocumentActivateEvent evt) {				
		ActiveReadingInterval activeInterval = new ActiveReadingInterval(evt.getPart());
		currentReadingInterval = activeInterval;
		addNewIntervalHandlers(activeInterval, PrefPage.getTimeOutReading());
	}
	
	private void addNewIntervalHandlers(final ActiveInterval interval, int timeout){
		interval.addTimeoutListener(timeout, new RunCallBack() {					
			@Override
			public void onInactive() {
				closeCurrentInterval(interval);
			}
		});
		IntervalNotifier.fireOnNewInterval(new NewIntervalEvent(interval));
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
	
	@Override
	public void closeAllCurrentIntervals(){
		if(currentEditingInterval != null)
			closeCurrentInterval(currentEditingInterval);
		if(currentReadingInterval != null)
			closeCurrentInterval(currentReadingInterval);
	}
	
}
