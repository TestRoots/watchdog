package nl.tudelft.watchdog.logic.ui.listeners;

import nl.tudelft.watchdog.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.JUnitEvent;

import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestRunSession;

/** A listener to the execution of Junit test via the IDE. */
public class JUnitListener {

	/** Constructor. */
	public JUnitListener(final EventManager eventManager) {

		JUnitCore.addTestRunListener(new TestRunListener() {
			@Override
			public void sessionFinished(ITestRunSession session) {
				super.sessionFinished(session);
				JUnitInterval interval = new JUnitInterval();
				// When aborted duration is NaN
				interval.calculateAndSetDates(session.getElapsedTimeInSeconds());
				interval.setProjectName(session.getTestRunName());
				interval.setResult(session.getTestResult(true));
				interval.countTests(session);

				eventManager.update(new JUnitEvent(interval));
			}
		});
	}
}
