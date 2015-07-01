package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.io.Serializable;
import java.util.ArrayList;

import nl.tudelft.watchdog.core.logic.network.JsonifiedDouble;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;

import com.google.gson.annotations.SerializedName;

/** JUnit execution representation in a tree-structure. */
public class JUnitExecution implements Serializable {

	/** JUnitExecution */
	private static final long serialVersionUID = 2L;

	/** The project on which the JUnit test was executed. */
	@SerializedName("p")
	private String projectHash;

	/** The test class on which the JUnit test was executed. */
	@SerializedName("t")
	private String testClassHash;

	/** The test method on which the JUnit test was executed. */
	@SerializedName("m")
	private String testMethodHash;

	/** Result of the test run. When aborted duration is NaN. */
	@SerializedName("r")
	private String result;

	@SerializedName("d")
	private JsonifiedDouble duration;

	@SerializedName("c")
	private ArrayList<JUnitExecution> childrenExecutions;

	/** Constructor. */
	public JUnitExecution(ITestElement test, JUnitExecution parent) {
		double elapsedTime = test.getElapsedTimeInSeconds();
		if (parent == null) {
			// Avoids potential NPE by making this JUnitExecution its own parent
			// for the scope of the constructor.
			parent = this;
		}

		if (elapsedTime > 0.0) {
			duration = new JsonifiedDouble(elapsedTime);
		}

		setResult(test.getTestResult(true));

		if (test instanceof ITestRunSession) {
			ITestRunSession session = (ITestRunSession) test;
			setProjectNameHash(session.getTestRunName());
		}

		if (test instanceof ITestCaseElement) {
			ITestCaseElement testElement = (ITestCaseElement) test;
			testMethodHash = WatchDogUtils.createHash(testElement
					.getTestMethodName());
			parent.setClassNameHash(testElement.getTestClassName());
		} else if (test instanceof ITestElementContainer) {
			ITestElementContainer testContainer = (ITestElementContainer) test;
			childrenExecutions = createTree(testContainer);
		}
	}

	/**
	 * @return The result in a string form. O stands for OK, everything else is
	 *         a failed test result.
	 */
	public String getResult() {
		return result;
	}

	/** Sets the result. */
	public void setResult(ITestElement.Result result) {
		this.result = result.toString().substring(0, 1);
	}

	private void setClassNameHash(String testClassName) {
		this.testClassHash = WatchDogUtils.createFileNameHash(testClassName);
	}

	/** Sets the hashed project name the JUnit test run was executed on. */
	public void setProjectNameHash(String projectName) {
		this.projectHash = WatchDogUtils.createHash(projectName);
	}

	private ArrayList<JUnitExecution> createTree(ITestElementContainer session) {
		ArrayList<JUnitExecution> children = new ArrayList<JUnitExecution>();
		for (ITestElement testChild : session.getChildren()) {
			children.add(new JUnitExecution(testChild, this));
		}
		return children;
	}
}