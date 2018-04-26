package nl.tudelft.watchdog.intellij.logic.interval.intervaltypes;

import java.util.Date;

import com.intellij.execution.testframework.AbstractTestProxy;

import com.google.gson.annotations.SerializedName;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;

/**
 * Data object containing information on JUnit test runs. Contains a
 * {@link nl.tudelft.watchdog.intellij.logic.interval.intervaltypes.JUnitExecution} object, which is a tree data structure. Hence, every
 * {@link nl.tudelft.watchdog.intellij.logic.interval.intervaltypes.JUnitExecution} can contain any number of child {@link nl.tudelft.watchdog.intellij.logic.interval.intervaltypes.JUnitExecution}
 * s.
 */
public class JUnitInterval extends IntervalBase {

	/** Class version. */
	private static final long serialVersionUID = 1L;

	@SerializedName("je")
	private JUnitExecution testExecution;

	/**
	 * Constructor. JUnit intervals are by definition closed (or non-existent
	 * yet), as they report on finished JUnit executions.
	 */
	public JUnitInterval(AbstractTestProxy test) {
		super(IntervalType.JUNIT, new Date());
		isClosed = true;
		double duration = test.getDuration() / 1000.0;

		setEndTime(new Date());
		if (!Double.isNaN(duration)) {
			setStartTime(new Date(new Date().getTime()
					- roundElapsedTime(duration)));
		}
		testExecution = new JUnitExecution(test, null);
	}

	private long roundElapsedTime(double duration) {
		return Math.round(duration * 1000);
	}

	/**
	 * @return The aggregated execution result of this Junit execution.
	 */
	public ExecutionResult getExecutionResult() {
		if (testExecution.getResult().equals("O")) {
			return ExecutionResult.OK;
		} else {
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
