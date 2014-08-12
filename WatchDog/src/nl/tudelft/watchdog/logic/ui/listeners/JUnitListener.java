package nl.tudelft.watchdog.logic.ui.listeners;

import java.util.Date;

import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.JUnitEvent;

import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;

/** A listener to the execution of Junit test via the IDE. */
public class JUnitListener {

	/** Constructor. */
	public JUnitListener(final EventManager eventManager) {

		JUnitCore.addTestRunListener(new TestRunListener() {
			@Override
			public void sessionFinished(ITestRunSession session) {
				super.sessionFinished(session);
				JUnitResults results = new JUnitResults();
				// When aborted duration is NaN
				results.duration = session.getElapsedTimeInSeconds();
				results.calculateAndSetDates();
				results.projectName = session.getTestRunName();
				results.result = session.getTestResult(true);
				results.countTests(session);

				eventManager.update(new JUnitEvent(results));
			}

		}

		);

	}

	/** Data object containing information on JUnit test runs. */
	public class JUnitResults {

		/** When the Junit run started. Might be <code>null</code>! */
		public Date beginDate;

		/** When the Junit run ended. Might be <code>null</code>! */
		public Date endDate;

		/** The project on which the JUnit test was executed. */
		public String projectName;

		/**
		 * The duration of the test run in seconds. When aborted duration is NaN
		 */
		public double duration;

		/** Result of the test run. When aborted duration is NaN. */
		public ITestElement.Result result;

		/** Number of test cases. */
		public int executedTestCases = 0;

		/** Number of executed test cases. */
		public int totalTestCases = 0;

		private void countTests(ITestElement testElement) {
			if (testElement instanceof ITestCaseElement) {
				totalTestCases += 1;
				if (testElement.getProgressState() == ITestElement.ProgressState.COMPLETED) {
					executedTestCases += 1;
				}
			} else if (testElement instanceof ITestElementContainer) {
				ITestElementContainer testContainer = (ITestElementContainer) testElement;
				for (ITestElement childTestElement : testContainer
						.getChildren()) {
					countTests(childTestElement);
				}
			}
		}

		private void calculateAndSetDates() {
			if (!Double.isNaN(duration)) {
				endDate = new Date();
				beginDate = new Date(endDate.getTime()
						- Math.round(duration * 1000));
			}
		}

	}
}
