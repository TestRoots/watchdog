package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.io.Serializable;
import java.util.ArrayList;

import com.intellij.rt.execution.junit.states.PoolOfTestStates;
import nl.tudelft.watchdog.core.logic.network.JsonifiedDouble;
import nl.tudelft.watchdog.util.WatchDogUtils;

import com.intellij.execution.testframework.AbstractTestProxy;

import com.google.gson.annotations.SerializedName;

/**
 * JUnit execution representation in a tree-structure.
 */
public class JUnitExecution implements Serializable {

    /**
     * Result states of a test.
     */
    enum Result {
        UNDEFINED("Undefined"),
        OK("OK"),
        ERROR("Error"),
        FAILURE("Failure"),
        IGNORED("Ignored");

        private String name;

        Result(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * JUnitExecution
     */
    private static final long serialVersionUID = 1L;

    /**
     * The project on which the JUnit test was executed.
     */
    @SerializedName("p")
    private String projectHash;

    /**
     * The test class on which the JUnit test was executed.
     */
    @SerializedName("t")
    private String testClassHash;

    /**
     * The test method on which the JUnit test was executed.
     */
    @SerializedName("m")
    private String testMethodHash;

    /**
     * Result of the test run. When aborted duration is NaN.
     */
    @SerializedName("r")
    private String result;

    @SerializedName("d")
    private JsonifiedDouble duration;

    @SerializedName("c")
    private ArrayList<JUnitExecution> childrenExecutions;

    /**
     * Constructor.
     */
    public JUnitExecution(AbstractTestProxy testProxy, JUnitExecution parent) {
        double elapsedTime = testProxy.getDuration() / 1000.0;
        if (elapsedTime > 0.0) {
            duration = new JsonifiedDouble(elapsedTime);
        }

        setResult(determineTestResult(testProxy));

        if (parent == null) {
            // Test run session
            setProjectNameHash(testProxy.getName());

            if (!testProxy.getChildren().isEmpty() && testProxy.getChildren().get(0).isLeaf()) {
                childrenExecutions = createTree(testProxy.getParent());
            } else {
                childrenExecutions = createTree(testProxy);
            }
            return;
        }

        if (testProxy.isLeaf()) {
            // Test case
            testMethodHash = WatchDogUtils.createHash(testProxy.getName());
            parent.setClassNameHash(testProxy.getParent().getName());
        } else {
            // Test container (class)
            childrenExecutions = createTree(testProxy);
        }

    }

    /**
     * @return The Result of the Test
     */
    private Result determineTestResult(AbstractTestProxy test) {
        if (test.isPassed()) { // getMagnitude() <= PoolOfTestStates.PASSED_INDEX
            return Result.OK;
        } else if (test.getMagnitude() == PoolOfTestStates.IGNORED_INDEX) {
            return Result.IGNORED;
        } else if (test.getMagnitude() == PoolOfTestStates.ERROR_INDEX) {
            return Result.ERROR;
        } else if (test.getMagnitude() == PoolOfTestStates.FAILED_INDEX || test.getMagnitude() == PoolOfTestStates.COMPARISON_FAILURE) {
            return Result.FAILURE;
        } else return Result.UNDEFINED;
    }

    /**
     * @return The result in a string form. O stands for OK, everything else is
     * a failed test result.
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the result.
     */
    public void setResult(Result result) {
        this.result = result.toString().substring(0, 1);
    }

    private void setClassNameHash(String testClassName) {
        this.testClassHash = WatchDogUtils.createFileNameHash(testClassName);
    }

    /**
     * Sets the hashed project name the JUnit test run was executed on.
     */
    public void setProjectNameHash(String projectName) {
        this.projectHash = WatchDogUtils.createHash(projectName);
    }

    private ArrayList<JUnitExecution> createTree(AbstractTestProxy session) {
        ArrayList<JUnitExecution> children = new ArrayList<JUnitExecution>();
        for (AbstractTestProxy testChild : session.getChildren()) {
            children.add(new JUnitExecution(testChild, this));
        }
        return children;
    }
}
