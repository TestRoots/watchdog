package nl.tudelft.watchdog.intellij.logic.breakpoint;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;

/**
 * Estimates the nature of a breakpoint into one of {@link BreakpointType}.
 */
public class BreakpointClassifier {

    /**
     * Classifies the breakpoint type of the given breakpoint by analyzing its
     * class name.TODO!!!!
     */
    public static BreakpointType classify(XBreakpoint breakpoint) {
        //TODO
        return BreakpointType.UNDEFINED;
    }
}
