package nl.tudelft.watchdog.intellij.logic.breakpoint;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;

/**
 * A factory for creating {@link Breakpoint}s from a supplied
 * {@link XBreakpoint}.
 */
public class BreakpointCreator {

    /**
     * Factory method that creates and returns a {@link Breakpoint} from a given
     * {@link XBreakpoint}.
     */
    public static Breakpoint createBreakpoint(XBreakpoint breakpoint) {
        Breakpoint result = new Breakpoint(breakpoint.hashCode(),
                BreakpointClassifier.classify(breakpoint));
        //TODO
        return result;
    }
}
