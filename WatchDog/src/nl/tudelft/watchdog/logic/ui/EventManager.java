package nl.tudelft.watchdog.logic.ui;

import java.util.Timer;
import java.util.TimerTask;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval.Perspective;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent.EventType;

/**
 * Manager for {@link EditorEvent}s. Links such events to actions in the
 * IntervalManager, i.e. manages the creation and deletion of intervals based on
 * the incoming events. Is a state machine.
 */
public class EventManager {

	private static final int ACTIVITY_TIMEOUT = 10000;
	/** The {@link IntervalManager} this observer is working with. */
	private IntervalManager intervalManager;
	private Timer activityTimer;
	private TimerTask inactivityTask;

	/** Constructor. */
	public EventManager(IntervalManager intervalManager) {
		this.intervalManager = intervalManager;
		activityTimer = new Timer(true);
		inactivityTask = new TimerTask() {
			@Override
			public void run() {
				update(new WatchDogEvent(this, EventType.INACTIVITY));
			}
		};
	}

	/** Introduces the supplied editorEvent */
	public void update(WatchDogEvent event) {
		IntervalBase interval;
		switch (event.getType()) {
		case START_ECLIPSE:
			intervalManager.addInterval(new IntervalBase(
					IntervalType.ECLIPSE_OPEN));
			break;
		case END_ECLIPSE:
			intervalManager.closeAllIntervals();
			break;
		case ACTIVE_WINDOW:
			intervalManager.addInterval(new IntervalBase(
					IntervalType.ECLIPSE_ACTIVE));
			break;
		case END_WINDOW:
			interval = intervalManager
					.getIntervalOfType(IntervalType.ECLIPSE_ACTIVE);
			intervalManager.closeInterval(interval);
			break;
		case START_JAVA_PERSPECTIVE:
			createNewPerspectiveInterval(Perspective.JAVA);
			break;
		case START_DEBUG_PERSPECTIVE:
			createNewPerspectiveInterval(Perspective.DEBUG);
			break;
		case START_UNKNOWN_PERSPECTIVE:
			createNewPerspectiveInterval(Perspective.OTHER);
			break;
		case JUNIT:
			JUnitInterval junitInterval = (JUnitInterval) event.getSource();
			intervalManager.addInterval(junitInterval);
			break;
		case ACTIVITY:
			// Performance optimization: only update activity timer every
			// second. This means that we have 10% imprecision, ie. the user may
			// have actually stayed inactive shorter than we think he did.
			if (inactivityTask.scheduledExecutionTime()
					- System.currentTimeMillis() < 0.9 * ACTIVITY_TIMEOUT) {
				inactivityTask.cancel();
				activityTimer.schedule(inactivityTask, ACTIVITY_TIMEOUT);
				break;
			}
			interval = intervalManager
					.getIntervalOfType(IntervalType.USER_ACTIVE);
			if (interval == null) {
				intervalManager.addInterval(new IntervalBase(
						IntervalType.USER_ACTIVE));
			}
			break;
		case INACTIVITY:
			interval = intervalManager
					.getIntervalOfType(IntervalType.USER_ACTIVE);
			intervalManager.closeInterval(interval);
			break;
		default:
			break;
		}

		WatchDogLogger.getInstance().logInfo("Event " + event.getType());
	}

	/** Creates a new perspective Interval of the given type. */
	public void createNewPerspectiveInterval(
			PerspectiveInterval.Perspective perspecitveType) {
		PerspectiveInterval perspectiveInterval = (PerspectiveInterval) intervalManager
				.getIntervalOfType(IntervalType.PERSPECTIVE);
		if (perspectiveInterval != null
				&& perspectiveInterval.getPerspectiveType() == perspecitveType) {
			// abort if such an interval is already open.
			return;
		}
		intervalManager.closeInterval(perspectiveInterval);
		intervalManager.addInterval(new PerspectiveInterval(perspecitveType));
	}
}