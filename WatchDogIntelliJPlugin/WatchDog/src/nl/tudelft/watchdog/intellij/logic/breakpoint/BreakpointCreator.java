package nl.tudelft.watchdog.intellij.logic.breakpoint;

import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import org.jetbrains.java.debugger.breakpoints.properties.JavaBreakpointProperties;

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

        // Initialize enabled and SuspendPolicy fields.
        result.setEnabled(breakpoint.isEnabled());
        result.setSuspendPolicy(breakpoint.getSuspendPolicy().ordinal());

        // Initialize hit count field.
        result.setHitCount(-1);
        if (breakpoint.getProperties() instanceof JavaBreakpointProperties) {
            JavaBreakpointProperties properties = (JavaBreakpointProperties) breakpoint.getProperties();
            if (properties.isCOUNT_FILTER_ENABLED()) {
                result.setHitCount(properties.getCOUNT_FILTER());
            }
        }

        // Initialize condition fields if available.
        XExpression condition = breakpoint.getConditionExpression();
        if (condition != null) {
            result.setCondition(condition.getExpression());
            result.setConditionEnabled(true);
        }
        return result;
    }
}
