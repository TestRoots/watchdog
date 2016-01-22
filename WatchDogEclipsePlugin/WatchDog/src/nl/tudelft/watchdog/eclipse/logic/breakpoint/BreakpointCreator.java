package nl.tudelft.watchdog.eclipse.logic.breakpoint;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;

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

			// Make sure the hit count is initialized.
			result.setHitCount(-1);
			if (breakpoint instanceof IJavaBreakpoint) {
				IJavaBreakpoint bp = (IJavaBreakpoint) breakpoint;
				result.setHitCount(bp.getHitCount());
			}
		} catch (CoreException exception) {
			exception.printStackTrace();
		}
		return result;
	}

}
