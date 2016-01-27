package nl.tudelft.watchdog.intellij.logic.breakpoint;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;

/**
 * Estimates the nature of a breakpoint into one of {@link BreakpointType}.
 */
public class BreakpointClassifier {

    /**
     * Classifies the breakpoint type of the given breakpoint by analyzing its
     * type. Note: class prepare breakpoints not available in IntelliJ.
     */
    public static BreakpointType classify(XBreakpoint breakpoint) {
        switch (breakpoint.getType().getId()) {
            case "java-line":
                return BreakpointType.LINE;

            case "java-method":
                return BreakpointType.METHOD;

            case "java-exception":
                return BreakpointType.EXCEPTION;

            case "java-field":
                return BreakpointType.FIELD;

            default:
                return BreakpointType.UNDEFINED;

        }
    }
}
