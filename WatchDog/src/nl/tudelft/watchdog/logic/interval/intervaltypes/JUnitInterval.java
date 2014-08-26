package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;

import com.google.gson.annotations.SerializedName;

/** Data object containing information on JUnit test runs. */
public class JUnitInterval extends IntervalBase {

	/**
	 * Constructor. JUnit intervals are by definition closed (or non-existent
	 * yet), as they report on finished JUnit executions.
	 */
	public JUnitInterval() {
		super(IntervalType.JUNIT);
		isClosed = true;
	}

	/** Class version. */
	private static final long serialVersionUID = 1L;

	/** The project on which the JUnit test was executed. */
	@SerializedName("pn")
	private String projectHash;

	/** Result of the test run. When aborted duration is NaN. */
	@SerializedName("re")
	private String result;

	/** Number of test cases. */
	@SerializedName("etests")
	private int executedTestCases = 0;

	/** Number of executed test cases. */
	@SerializedName("ttests")
	private int totalTestCases = 0;

	/**
	 * Counts and sets the number of tests contained in the given
	 * {@link ITestElement}.
	 */
	public void countTests(ITestElement testElement) {
		if (testElement instanceof ITestCaseElement) {
			totalTestCases += 1;
			if (testElement.getProgressState() == ITestElement.ProgressState.COMPLETED) {
				executedTestCases += 1;
			}
		} else if (testElement instanceof ITestElementContainer) {
			ITestElementContainer testContainer = (ITestElementContainer) testElement;
			for (ITestElement childTestElement : testContainer.getChildren()) {
				countTests(childTestElement);
			}
		}
	}

	/** Calculates and sets the dates in this interval. */
	public void calculateAndSetDates(double duration) {
		if (!Double.isNaN(duration)) {
			setEndTime(new Date());
			setStartTime(new Date(getEnd().getTime()
					- Math.round(duration * 1000)));
		}
	}

	/** Sets the result. */
	public void setResult(ITestElement.Result result) {
		this.result = result.toString();
	}

	/** Sets the project name the Junit test run was executed on. */
	public void setProjectName(String projectName) {
		this.projectHash = WatchDogUtils.createHash(projectName);
	}

}