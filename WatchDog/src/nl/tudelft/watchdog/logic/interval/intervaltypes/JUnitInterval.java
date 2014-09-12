package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;

import com.google.gson.annotations.SerializedName;

/**
 * Data object containing information on JUnit test runs. This is a tree data
 * structure. Hence, every JunitInterval can contain any number of child
 * JUnitIntervals.
 */
public class JUnitInterval extends IntervalBase {

	/** Class version. */
	private static final long serialVersionUID = 1L;

	/** The project on which the JUnit test was executed. */
	@SerializedName("pn")
	private String projectHash;

	/** The test class on which the JUnit test was executed. */
	@SerializedName("tn")
	private String testClassHash;

	/** Result of the test run. When aborted duration is NaN. */
	@SerializedName("re")
	private String result;

	@SerializedName("ch")
	private ArrayList<JUnitInterval> childrenIntervals;

	/**
	 * Constructor. JUnit intervals are by definition closed (or non-existent
	 * yet), as they report on finished JUnit executions.
	 */
	public JUnitInterval(ITestElement test) {
		super(IntervalType.JUNIT);
		double duration = test.getElapsedTimeInSeconds();
		if (!Double.isNaN(duration)) {
			setStartTime(new Date(new Date().getTime()
					- roundElapsedTime(duration)));
		}
		init(test);
	}

	/**
	 * Constructor. JUnit intervals are by definition closed (or non-existent
	 * yet), as they report on finished JUnit executions.
	 */
	public JUnitInterval(ITestElement test, Date start) {
		super(IntervalType.JUNIT);
		setStartTime(start);
		init(test);
	}

	private void init(ITestElement test) {
		isClosed = true;

		Date startDate = getStart();
		if (startDate != null) {
			long roundedDuration = roundElapsedTime(test
					.getElapsedTimeInSeconds());
			setEndTime(new Date(startDate.getTime() + roundedDuration));
		}

		setResult(test.getTestResult(true));

		if (test instanceof ITestRunSession) {
			ITestRunSession session = (ITestRunSession) test;
			setProjectNameHash(session.getTestRunName());
		}

		if (test instanceof ITestCaseElement) {
			ITestCaseElement testElement = (ITestCaseElement) test;
			setClassNameHash(testElement.getTestClassName());
		} else if (test instanceof ITestElementContainer) {
			ITestElementContainer testContainer = (ITestElementContainer) test;
			createTree(testContainer);
		}
	}

	private long roundElapsedTime(double duration) {
		return Math.round(duration * 1000);
	}

	/** Sets the result. */
	public void setResult(ITestElement.Result result) {
		this.result = result.toString();
	}

	private void setClassNameHash(String testClassName) {
		this.testClassHash = WatchDogUtils.createFileNameHash(testClassName);
	}

	/** Sets the hashed project name the Junit test run was executed on. */
	public void setProjectNameHash(String projectName) {
		this.projectHash = WatchDogUtils.createHash(projectName);
	}

	private void createTree(ITestElementContainer session) {
		childrenIntervals = new ArrayList<JUnitInterval>();
		Date startDate = getStart();
		for (ITestElement testChild : session.getChildren()) {
			JUnitInterval childInterval = new JUnitInterval(testChild,
					startDate);
			childrenIntervals.add(childInterval);
			startDate = new Date(startDate.getTime()
					+ childInterval.getDuration().getMillis());
		}
	}
}