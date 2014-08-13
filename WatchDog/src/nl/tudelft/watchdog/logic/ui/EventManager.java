package nl.tudelft.watchdog.logic.ui;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval.Perspective;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

/**
 * Manager for {@link EditorEvent}s. Links such events to actions in the
 * IntervalManager, i.e. manages the creation and deletion of intervals based on
 * the incoming events. Is a state machine.
 */
public class EventManager {

	/** The {@link IntervalManager} this observer is working with. */
	private IntervalManager intervalManager;

	private WatchDogEvent previousEvent;

	/** Constructor. */
	public EventManager(IntervalManager intervalManager) {
		this.intervalManager = intervalManager;
	}

	/** Introduces the supplied editorEvent */
	public void update(WatchDogEvent event) {
		// fast exit strategy on double events
		if (event == previousEvent) {
			return;
		}
		previousEvent = event;

		switch (event.getType()) {
		case START_ECLIPSE:
			intervalManager.addInterval(new IntervalBase(
					IntervalType.ECLIPSE_OPEN));
			break;
		case END_ECLIPSE:
			intervalManager.closeAllCurrentIntervals();
			break;
		case ACTIVE_WINDOW:
			intervalManager.addInterval(new IntervalBase(
					IntervalType.ECLIPSE_ACTIVE));
			break;
		case END_WINDOW:
			IntervalBase interval = intervalManager
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