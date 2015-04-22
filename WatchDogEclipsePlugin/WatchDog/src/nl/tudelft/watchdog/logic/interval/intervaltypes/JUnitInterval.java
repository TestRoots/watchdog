package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jdt.junit.model.ITestElement;

import com.google.gson.annotations.SerializedName;

/**
 * Data object containing information on JUnit test runs. Contains a
 * {@link JUnitExecution} object, which is a tree data structure. Hence, every
 * {@link JUnitExecution} can contain any number of child {@link JUnitExecution}
 * s.
 */
public class JUnitInterval extends IntervalBase {

	/** Class version. */
	private static final long serialVersionUID = 2L;

	@SerializedName("je")
	private final JUnitExecution testExecution;

	/**
	 * Constructor. JUnit intervals are by definition closed (or non-existent
	 * yet), as they report on finished JUnit executions.
	 */
	public JUnitInterval(ITestElement test) {
		super(IntervalType.JUNIT, new Date());
		isClosed = true;
		double duration = test.getElapsedTimeInSeconds();

		setEndTime(new Date());
		if (!Double.isNaN(duration)) {
			setStartTime(new Date(new Date().getTime()
					- roundElapsedTime(duration)));
		}
		testExecution = new JUnitExecution(test, null);

		ArrayList<IntervalBase> interval = new ArrayList<IntervalBase>();
		interval.add(this);
	}

	private long roundElapsedTime(double duration) {
		return Math.round(duration * 1000);
	}

	/**
	 * @return The aggregated execution result of this Junit execution.
	 */
	public ExecutionResult getExecutionResult() {
		switch (testExecution.getResult()) {
		case "O":
			return ExecutionResult.OK;
		default:
			return ExecutionResult.FAILURE;
		}
	}

	/** Denotes the execution result. */
	public enum ExecutionResult {
		/** Test passed */
		OK,

		/** Test failed */
		FAILURE
	}
}