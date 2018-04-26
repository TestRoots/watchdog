package nl.tudelft.watchdog.logic.breakpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeClassifier;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;

/**
 * Test class for {@link BreakpointChangeClassifier}'s classify method to make
 * sure breakpoint changes are classified correctly.
 */
public class BreakpointChangeClassifierTest {

	@Test
	public void classification_with_old_break_point_null_works() {
		Breakpoint bp = createBreakpoint();
		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(null, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.UNKNOWN, result.get(0));
	}

	@Test
	public void classification_with_new_breakpoint_null_works() {
		Breakpoint bp = createBreakpoint();
		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(bp, null);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.UNKNOWN, result.get(0));
	}

	@Test
	public void equivalent_breakpoint_should_not_change() {
		Breakpoint bp = createBreakpoint();
		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(bp, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.UNKNOWN, result.get(0));
	}

	@Test
	public void breakpoint_enabled() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setEnabled(false);
		bp.setEnabled(true);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.ENABLED, result.get(0));
	}

	@Test
	public void breakpoint_disabled() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setEnabled(true);
		bp.setEnabled(false);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.DISABLED, result.get(0));
	}

	@Test
	public void breakpoint_hitcount_added() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setHitCount(-1);
		bp.setHitCount(1);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.HC_ADDED, result.get(0));
	}

	@Test
	public void breakpoint_hitcount_removed() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setHitCount(1);
		bp.setHitCount(-1);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.HC_REMOVED, result.get(0));
	}

	@Test
	public void breakpoint_hitcount_changed() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setHitCount(1);
		bp.setHitCount(2);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.HC_CHANGED, result.get(0));
	}

	@Test
	public void breakpoint_suspendpolicy_changed() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setSuspendPolicy(0);
		bp.setSuspendPolicy(1);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.SP_CHANGED, result.get(0));
	}

	@Test
	public void breakpoint_condition_enabled() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setConditionEnabled(false);
		bp.setConditionEnabled(true);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.COND_ENABLED, result.get(0));
	}

	@Test
	public void breakpoint_condition_disabled() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setConditionEnabled(true);
		bp.setConditionEnabled(false);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.COND_DISABLED, result.get(0));
	}

	@Test
	public void breakpoint_condition_added() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setCondition(null);
		bp.setCondition("cond");

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.UNKNOWN, result.get(0));
	}

	@Test
	public void breakpoint_condition_removed() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setCondition("cond");
		bp.setCondition(null);

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.COND_CHANGED, result.get(0));
	}

	@Test
	public void breakpoint_condition_changed() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setCondition("cond_old");
		bp.setCondition("cond");

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.COND_CHANGED, result.get(0));
	}

	@Test
	public void breakpoint_condition_no_changes() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setCondition("cond");
		bp.setCondition("cond");

		List<BreakpointChangeType> result = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(1, result.size());
		assertEquals(BreakpointChangeType.UNKNOWN, result.get(0));
	}

	@Test
	public void breakpoint_two_changes() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setEnabled(false);
		bp.setEnabled(true);
		old.setHitCount(-1);
		bp.setHitCount(1);

		List<BreakpointChangeType> results = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(2, results.size());
		assertTrue(results.contains(BreakpointChangeType.ENABLED));
		assertTrue(results.contains(BreakpointChangeType.HC_ADDED));
	}

	@Test
	public void breakpoint_three_changes() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setEnabled(true);
		bp.setEnabled(false);
		old.setHitCount(1);
		bp.setHitCount(-1);
		old.setSuspendPolicy(0);
		bp.setSuspendPolicy(1);

		List<BreakpointChangeType> results = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(3, results.size());
		assertTrue(results.contains(BreakpointChangeType.DISABLED));
		assertTrue(results.contains(BreakpointChangeType.HC_REMOVED));
		assertTrue(results.contains(BreakpointChangeType.SP_CHANGED));
	}

	@Test
	public void breakpoint_three_changes_and_condition() {
		Breakpoint old = createBreakpoint();
		Breakpoint bp = createBreakpoint();
		old.setEnabled(true);
		bp.setEnabled(false);
		old.setConditionEnabled(true);
		bp.setConditionEnabled(false);
		old.setCondition("cond");
		bp.setCondition("cond_old");

		List<BreakpointChangeType> results = BreakpointChangeClassifier.classify(old, bp);
		assertEquals(3, results.size());
		assertTrue(results.contains(BreakpointChangeType.DISABLED));
		assertTrue(results.contains(BreakpointChangeType.COND_DISABLED));
		assertTrue(results.contains(BreakpointChangeType.COND_CHANGED));
	}

	private static Breakpoint createBreakpoint() {
		return new Breakpoint(0, BreakpointType.LINE);
	}

}
