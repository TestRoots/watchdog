package nl.tudelft.watchdog.eclipse.logic.breakpoint;

import org.eclipse.debug.core.model.IBreakpoint;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;

/**
 * Estimates the nature of a breakpoint into one of {@link BreakpointType}.
 */
public class BreakpointClassifier {

	/**
	 * Classifies the breakpoint type of the given breakpoint by analyzing its
	 * class name.
	 */
	public static BreakpointType classify(IBreakpoint breakpoint) {
		switch (breakpoint.getClass().getSimpleName()) {
		case "JavaLineBreakpoint":
			return BreakpointType.LINE;

		case "JavaMethodBreakpoint":
			return BreakpointType.METHOD;

		case "JavaExceptionBreakpoint":
			return BreakpointType.EXCEPTION;

		case "JavaClassPrepareBreakpoint":
			return BreakpointType.CLASS;

		case "JavaWatchpoint":
			return BreakpointType.FIELD;

		default:
			return BreakpointType.UNDEFINED;
		}
	}

}
