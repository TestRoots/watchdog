package nl.tudelft.watchdog.eclipse.logic.breakpoint;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;

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
		} catch (CoreException exception) {
			exception.printStackTrace();
		}
		return result;
	}

}
