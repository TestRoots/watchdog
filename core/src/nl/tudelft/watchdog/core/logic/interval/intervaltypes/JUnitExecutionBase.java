package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.network.JsonifiedDouble;
import nl.tudelft.watchdog.core.util.WatchDogUtilsBase;

/** JUnit execution representation in a tree-structure. */
public class JUnitExecutionBase implements Serializable {

	/** JUnitExecution */
	protected static final long serialVersionUID = 2L;

	/** The project on which the JUnit test was executed. */
	@SerializedName("p")
	protected String projectHash;
	/** The test class on which the JUnit test was executed. */
	@SerializedName("t")
	protected String testClassHash;
	/** The test method on which the JUnit test was executed. */
	@SerializedName("m")
	protected String testMethodHash;
	/** Result of the test run. When aborted duration is NaN. */
	@SerializedName("r")
	protected String result;
	@SerializedName("d")
	protected JsonifiedDouble duration;
	@SerializedName("c")
	protected ArrayList<JUnitExecutionBase> childrenExecutions;

	public JUnitExecutionBase() {
		super();
	}

	/**
	 * @return The result in a string form. O stands for OK, everything else is
	 *         a failed test result.
	 */
	public String getResult() {
		return result;
	}

	protected void setClassNameHash(String testClassName) {
		this.testClassHash = WatchDogUtilsBase.createFileNameHash(testClassName);
	}

	/** Sets the hashed project name the JUnit test run was executed on. */
	public void setProjectNameHash(String projectName) {
		this.projectHash = WatchDogUtilsBase.createHash(projectName);
	}

}
