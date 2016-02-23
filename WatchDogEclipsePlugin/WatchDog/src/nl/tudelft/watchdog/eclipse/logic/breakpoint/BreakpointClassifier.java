package nl.tudelft.watchdog.eclipse.logic.breakpoint;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.IJavaClassPrepareBreakpoint;
import org.eclipse.jdt.debug.core.IJavaExceptionBreakpoint;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaMethodBreakpoint;
import org.eclipse.jdt.debug.core.IJavaWatchpoint;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;

/**
 * Estimates the nature of a breakpoint into one of {@link BreakpointType}.
 */
public class BreakpointClassifier {

	/**
	 * Classifies the breakpoint type of the given breakpoint by analyzing its
	 * class name.
	 * 
	 * WARNING: Do not change the ordering of the if-statements if you don't
	 * have to!
	 */
	public static BreakpointType classify(IBreakpoint breakpoint) {
		if (breakpoint instanceof IJavaWatchpoint) {
			return BreakpointType.FIELD;
		} else if (breakpoint instanceof IJavaMethodBreakpoint) {
			return BreakpointType.METHOD;
		} else if (breakpoint instanceof IJavaClassPrepareBreakpoint) {
			return BreakpointType.CLASS;
		} else if (breakpoint instanceof IJavaLineBreakpoint) {
			return BreakpointType.LINE;
		} else if (breakpoint instanceof IJavaExceptionBreakpoint) {
			return BreakpointType.EXCEPTION;
		} else {
			return BreakpointType.UNDEFINED;
		}
	}
}
