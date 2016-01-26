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

		// Check for changes in the hit count of the breakpoint.
		if (old_bp.getHitCount() != new_bp.getHitCount()) {
			if (new_bp.getHitCount() != -1) {
				if (old_bp.getHitCount() == -1) {
					changes.add(BreakpointChangeType.HC_ADDED);
				} else {
					changes.add(BreakpointChangeType.HC_CHANGED);
				}
			} else {
				changes.add(BreakpointChangeType.HC_REMOVED);
			}
		}

		// Check for changes in the suspend policy of the breakpoint.
		if (old_bp.getSuspendPolicy() != new_bp.getSuspendPolicy()) {
			changes.add(BreakpointChangeType.SP_CHANGED);
		}

		// Check for changes in the enablement of the condition of the
		// breakpoint.
		if (old_bp.isConditionEnabled() != new_bp.isConditionEnabled()) {
			if (new_bp.isConditionEnabled()) {
				changes.add(BreakpointChangeType.COND_ENABLED);
			} else {
				changes.add(BreakpointChangeType.COND_DISABLED);
			}
		}

		// Check for changes in the condition of the breakpoint.
		if (old_bp.getCondition() != null) {
			if (!old_bp.getCondition().equals(new_bp.getCondition())) {
				changes.add(BreakpointChangeType.COND_CHANGED);
			}
		}

		// TODO: support more types of changes

		// If no changes are identified at this point, add UNKNOWN change.
		if (changes.size() == 0) {
			changes.add(BreakpointChangeType.UNKNOWN);
		}
		return changes;
	}

}
