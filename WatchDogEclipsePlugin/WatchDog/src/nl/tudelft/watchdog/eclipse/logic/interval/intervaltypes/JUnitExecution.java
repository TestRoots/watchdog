package nl.tudelft.watchdog.eclipse.logic.interval.intervaltypes;

import java.util.ArrayList;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.JUnitExecutionBase;
import nl.tudelft.watchdog.core.logic.network.JsonifiedDouble;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/** Eclipse-specific JUnit execution representation in a tree-structure. */
public class JUnitExecution extends JUnitExecutionBase {

	/** Class version. */
	private static final long serialVersionUID = 1L;

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
			// FIXME (MMB) is test class name, but redundant! Can be replaced by
			// project name
			setProjectNameHash(session.getTestRunName());
		}

		if (test instanceof ITestCaseElement) {
			ITestCaseElement testElement = (ITestCaseElement) test;
			testMethodHash = WatchDogUtils
					.createHash(testElement.getTestMethodName());
			parent.setClassNameHash(testElement.getTestClassName());
		} else if (test instanceof ITestElementContainer) {
			ITestElementContainer testContainer = (ITestElementContainer) test;
			childrenExecutions = createTree(testContainer);
		}
	}

	/** Sets the result. */
	public void setResult(ITestElement.Result result) {
		this.result = result.toString().substring(0, 1);
	}

	private ArrayList<JUnitExecutionBase> createTree(
			ITestElementContainer session) {
		ArrayList<JUnitExecutionBase> children = new ArrayList<JUnitExecutionBase>();
		for (ITestElement testChild : session.getChildren()) {
			children.add(new JUnitExecution(testChild, this));
		}
		return children;
	}
}