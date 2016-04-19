package nl.tudelft.watchdog.eclipse.logic.breakpoint;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;

/**
 * A factory for creating {@link Breakpoint}s from a supplied
 * {@link IBreakpoint}.
 */
public class BreakpointCreator {

	/**
	 * Factory method that creates and returns a {@link Breakpoint} from a given
	 * {@link IBreakpoint}.
	 */
	public static Breakpoint createBreakpoint(IBreakpoint breakpoint) {
		Breakpoint result = new Breakpoint(breakpoint.hashCode(),
				BreakpointClassifier.classify(breakpoint));

		try {
			result.setEnabled(breakpoint.isEnabled());

			// Make sure the hit count and suspend policy are initialized.
			result.setHitCount(-1);
			if (breakpoint instanceof IJavaBreakpoint) {
				IJavaBreakpoint bp = (IJavaBreakpoint) breakpoint;
				result.setSuspendPolicy(bp.getSuspendPolicy());
				result.setHitCount(bp.getHitCount());
			}

			// Initialize condition fields if available.
			if (breakpoint instanceof IJavaLineBreakpoint) {
				IJavaLineBreakpoint bp = (IJavaLineBreakpoint) breakpoint;
				result.setConditionEnabled(bp.isConditionEnabled());
				result.setCondition(bp.getCondition());
			}
		} catch (CoreException exception) {
			exception.printStackTrace();
		}
		return result;
	}

}
