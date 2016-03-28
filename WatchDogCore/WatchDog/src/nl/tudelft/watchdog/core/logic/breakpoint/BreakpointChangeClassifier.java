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
	public static List<BreakpointChangeType> classify(Breakpoint oldBreakpoint, Breakpoint newBreakpoint) {
		List<BreakpointChangeType> changes = new ArrayList<BreakpointChangeType>();
		if (oldBreakpoint == null || newBreakpoint == null) {
			// Old BP was added in a previous session, so the change(s) are
			// unknown.
			changes.add(BreakpointChangeType.UNKNOWN);
			return changes;
		}

		checkForEnablementChanges(oldBreakpoint, newBreakpoint, changes);
		checkForHitCountChanges(oldBreakpoint, newBreakpoint, changes);
		checkForSuspendPolicyChanges(oldBreakpoint, newBreakpoint, changes);
		checkForConditionChanges(oldBreakpoint, newBreakpoint, changes);

		// If no changes are identified at this point, add UNKNOWN change.
		if (changes.isEmpty()) {
			changes.add(BreakpointChangeType.UNKNOWN);
		}
		return changes;
	}

	/** Check for changes in breakpoint enablement. */
	private static void checkForEnablementChanges(Breakpoint oldBreakpoint, Breakpoint newBreakpoint,
			List<BreakpointChangeType> changes) {
		if (oldBreakpoint.isEnabled() != newBreakpoint.isEnabled()) {
			if (newBreakpoint.isEnabled()) {
				changes.add(BreakpointChangeType.ENABLED);
			} else {
				changes.add(BreakpointChangeType.DISABLED);
			}
		}
	}

	/** Check for changes in the hit count of the breakpoint. */
	private static void checkForHitCountChanges(Breakpoint oldBreakpoint, Breakpoint newBreakpoint,
			List<BreakpointChangeType> changes) {
		if (oldBreakpoint.getHitCount() != newBreakpoint.getHitCount()) {
			if (newBreakpoint.hitCountEnabled()) {
				BreakpointChangeType changeType = (!oldBreakpoint.hitCountEnabled()) ? BreakpointChangeType.HC_ADDED
						: BreakpointChangeType.HC_CHANGED;
				changes.add(changeType);
			} else {
				changes.add(BreakpointChangeType.HC_REMOVED);
			}
		}
	}

	/** Check for changes in the suspend policy of the breakpoint. */
	private static void checkForSuspendPolicyChanges(Breakpoint oldBreakpoint, Breakpoint newBreakpoint,
			List<BreakpointChangeType> changes) {
		if (oldBreakpoint.getSuspendPolicy() != newBreakpoint.getSuspendPolicy()) {
			changes.add(BreakpointChangeType.SP_CHANGED);
		}
	}

	/**
	 * Check for changes in the enablement of the condition of the breakpoint
	 * and the condition itself.
	 */
	private static void checkForConditionChanges(Breakpoint oldBreakpoint, Breakpoint newBreakpoint,
			List<BreakpointChangeType> changes) {
		if (oldBreakpoint.isConditionEnabled() != newBreakpoint.isConditionEnabled()) {
			if (newBreakpoint.isConditionEnabled()) {
				changes.add(BreakpointChangeType.COND_ENABLED);
			} else {
				changes.add(BreakpointChangeType.COND_DISABLED);
			}
		}

		if (oldBreakpoint.getCondition() != null) {
			if (conditionIsDifferent(oldBreakpoint, newBreakpoint)) {
				changes.add(BreakpointChangeType.COND_CHANGED);
			}
		}
	}

	/** @return whether or not the conditions of the breakpoints are different. */
	private static boolean conditionIsDifferent(Breakpoint oldBreakpoint, Breakpoint newBreakpoint) {
		return !oldBreakpoint.getCondition().equals(newBreakpoint.getCondition());
	}

}
