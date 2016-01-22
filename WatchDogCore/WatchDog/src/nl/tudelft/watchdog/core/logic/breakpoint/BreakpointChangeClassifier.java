package nl.tudelft.watchdog.core.logic.breakpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Estimates the nature of a breakpoint change into one of
 * {@link BreakpointChangeType}.
 */
public class BreakpointChangeClassifier {

	/**
	 * Classifies the breakpoint change type(s) by analyzing the differences
	 * between the properties of the old and new breakpoint.
	 */
	public static List<BreakpointChangeType> classify(Breakpoint old_bp, Breakpoint new_bp) {
		List<BreakpointChangeType> changes = new ArrayList<>();
		if (old_bp == null) {
			// Old BP was added in a previous session, so the change(s) are
			// unknown.
			changes.add(BreakpointChangeType.UNKNOWN);
			return changes;
		}

		// Check for changes in breakpoint enablement.
		if (old_bp.isEnabled() != new_bp.isEnabled()) {
			if (new_bp.isEnabled()) {
				changes.add(BreakpointChangeType.ENABLED);
			} else {
				changes.add(BreakpointChangeType.DISABLED);
			}
		}

		//TODO: support more types of changes
		
		// If no changes are identified at this point, add UNKNOWN change.
		if (changes.size() == 0) {
			changes.add(BreakpointChangeType.UNKNOWN);
		}
		return changes;
	}

}
