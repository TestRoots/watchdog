package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.eclipseuireader.UIListener;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentActivateEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentDeActivateEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentNotifier;
import nl.tudelft.watchdog.logic.eclipseuireader.events.IDocumentAttentionListener;
import nl.tudelft.watchdog.logic.interval.active.ActiveInterval;
import nl.tudelft.watchdog.logic.interval.active.ActiveReadingInterval;
import nl.tudelft.watchdog.logic.interval.active.ActiveTypingInterval;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;
import nl.tudelft.watchdog.logic.interval.events.ClosingIntervalEvent;
import nl.tudelft.watchdog.logic.interval.events.IIntervalListener;
import nl.tudelft.watchdog.logic.interval.events.IntervalNotifier;
import nl.tudelft.watchdog.logic.interval.events.NewIntervalEvent;
import nl.tudelft.watchdog.logic.interval.recorded.IInterval;
import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.util.WatchDogUtils;

/**
 * Implementation of an {@link IIntervalManager}. Is a singleton.
 */
public class IntervalManager extends IntervalNotifier implements
		IIntervalManager {

	private ActiveReadingInterval currentReadingInterval;
	private ActiveTypingInterval currentEditingInterval;
	private UIListener uiListener;
	private DocumentFactory documentFactory;

	/* The recorded intervals of this session */
	private List<IInterval> recordedIntervals;

	private static IntervalManager instance = null;

	/**
	 * Returns the existing or creates and returns a new {@link IntervalManager}
	 * instance.
	 */
	public static IntervalManager getInstance() {
		if (instance == null) {
			instance = new IntervalManager();
		}
		return instance;
	}

	private IntervalManager() {
		recordedIntervals = new ArrayList<IInterval>();

		listenToDocumentChanges();
		uiListener = new UIListener();
		uiListener.attachListeners();
		documentFactory = new DocumentFactory();
	}

	private void listenToDocumentChanges() {
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {

			@Override
			public void onDocumentStartEditing(final DocumentActivateEvent evt) {
				// create a new active interval when doc is new
				if (currentEditingInterval == null
						|| currentEditingInterval.isClosed()) {
					createNewEditingInterval(evt);
				} else if (currentEditingInterval.getEditor() != evt
						.getChangedEditor()) {
					closeCurrentInterval(currentEditingInterval);
					createNewEditingInterval(evt);
				}
			}

			@Override
			public void onDocumentStopEditing(DocumentDeActivateEvent evt) {
				if (currentEditingInterval != null
						&& evt.getChangedEditor() == currentEditingInterval
								.getEditor()) {
					closeCurrentInterval(currentEditingInterval);
				}
			}

			@Override
			public void onDocumentStartFocus(DocumentActivateEvent evt) {
				// create a new active interval when doc is new
				if (currentReadingInterval == null
						|| currentReadingInterval.isClosed()) {
					createNewReadingInterval(evt);
				} else if (currentReadingInterval.getEditor() != evt
						.getChangedEditor()) {
					closeCurrentInterval(currentReadingInterval);
					createNewReadingInterval(evt);
				}
			}

			@Override
			public void onDocumentEndFocus(DocumentDeActivateEvent evt) {
				// MessageConsoleManager.getConsoleStream().println("onDocumentEndFocus"
				// + evt.getChangedEditor().getTitle());
				if (currentReadingInterval != null
						&& evt.getChangedEditor() == currentReadingInterval
								.getEditor()) {
					closeCurrentInterval(currentReadingInterval);
				}
			}

		});
	}

	private void closeCurrentInterval(ActiveInterval interval) {
		if (!interval.isClosed()) {

			Document doc = documentFactory.createDocument(interval.getPart());
			RecordedInterval recordedInterval = new RecordedInterval(doc,
					interval.getTimeOfCreation(), new Date(),
					interval.getActivityType(), WatchDogUtils.isInDebugMode());
			recordedIntervals.add(recordedInterval);
			interval.closeInterval();
			IntervalNotifier.fireOnClosingInterval(new ClosingIntervalEvent(
					recordedInterval));
		}
	}

	private void createNewEditingInterval(final DocumentActivateEvent evt) {
		ActiveTypingInterval activeInterval = new ActiveTypingInterval(
				evt.getChangedEditor());
		currentEditingInterval = activeInterval;
		addNewIntervalHandlers(activeInterval, WatchdogPreferences
				.getInstance().getTypingTimeout());
	}

	private void createNewReadingInterval(final DocumentActivateEvent evt) {
		ActiveReadingInterval activeInterval = new ActiveReadingInterval(
				evt.getPart());
		currentReadingInterval = activeInterval;
		addNewIntervalHandlers(activeInterval, WatchdogPreferences
				.getInstance().getTimeOutReading());
	}

	private void addNewIntervalHandlers(final ActiveInterval interval,
			int timeout) {
		interval.addTimeoutListener(timeout, new OnInactiveCallBack() {
			@Override
			public void onInactive() {
				closeCurrentInterval(interval);
			}
		});
		IntervalNotifier.fireOnNewInterval(new NewIntervalEvent(interval));
	}

	@Override
	public void addIntervalListener(IIntervalListener listener) {
		IntervalNotifier.addMyEventListener(listener);
	}

	@Override
	public void removeIntervalListener(IIntervalListener listener) {
		IntervalNotifier.removeMyEventListener(listener);
	}

	@Override
	public List<IInterval> getRecordedIntervals() {
		return recordedIntervals;
	}

	@Override
	public void setRecordedIntervals(List<IInterval> intervals) {
		recordedIntervals = intervals;
	}

	@Override
	public void closeAllCurrentIntervals() {
		// close editing interval
		if (currentEditingInterval != null) {
			closeCurrentInterval(currentEditingInterval);
		}
		// close reading interval
		if (currentReadingInterval != null) {
			closeCurrentInterval(currentReadingInterval);
		}
	}

}
