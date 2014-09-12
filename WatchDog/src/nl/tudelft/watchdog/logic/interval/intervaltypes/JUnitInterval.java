package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestRunSession;

import com.google.gson.annotations.SerializedName;

/**
 * Data object containing information on JUnit test runs. This is a tree data
 * structure. Hence, every JunitInterval can contain any number of child
 * JUnitIntervals.
 */
public class JUnitInterval extends IntervalBase {

	/**
	 * Constructor. JUnit intervals are by definition closed (or non-existent
	 * yet), as they report on finished JUnit executions.
	 */
	public JUnitInterval(ITestElement test) {
		super(IntervalType.JUNIT);
		isClosed = true;

		calculateAndSetDates(test.getElapsedTimeInSeconds());
		setResult(test.getTestResult(true));
		if (test instanceof ITestRunSession) {
			ITestRunSession session = (ITestRunSession) test;
			setProjectNameHash(session.getTestRunName());
		}
	}

	/** Class version. */
	private static final long serialVersionUID = 1L;

	/** The project on which the JUnit test was executed. */
	@SerializedName("pn")
	private String projectHash;

	/** Result of the test run. When aborted duration is NaN. */
	@SerializedName("re")
	private String result;

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

	/** Sets the hashed project name the Junit test run was executed on. */
	public void setProjectNameHash(String projectName) {
		this.projectHash = WatchDogUtils.createHash(projectName);
	}

	public void setTree(ITestRunSession session) {
		for (ITestElement testChild : session.getChildren()) {
		}

	}
}