package nl.tudelft.watchdog.core.logic.breakpoint;

/**
 * Estimates the nature of a breakpoint change into one of
 * {@link BreakpointChangeType}.
 */
public class BreakpointChangeClassifier {

	/**
	 * Classifies the breakpoint change type by analyzing the differences
	 * between the properties of the old and new breakpoint.
	 */
	public static BreakpointChangeType classify(Breakpoint old_bp, Breakpoint new_bp) {
		if (old_bp == null) {
			// Old BP was added in a previous session, so the change(s) are
			// unknown.
			return BreakpointChangeType.UNKNOWN;
		}

		if (old_bp.isEnabled() != new_bp.isEnabled()) {
			if (new_bp.isEnabled()) {
				return BreakpointChangeType.ENABLED;
			} else {
				return BreakpointChangeType.DISABLED;
			}
		}
		return BreakpointChangeType.UNKNOWN;
	}

}
