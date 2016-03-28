package nl.tudelft.watchdog.logic.breakpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.eclipse.logic.breakpoint.BreakpointCreator;

/**
 * Tests class for {@link BreakpointCreator} to verify whether
 * {@link Breakpoint}s are created correctly based on different
 * {@link IBreakpoint}s.
 */
public class BreakpointCreatorTest {

	@Test
	public void testCreateBreakpointHash() {
		IBreakpoint bp = mock(IBreakpoint.class);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertEquals(bp.hashCode(), result.getHash());
	}

	@Test
	public void testCreateBreakpointType() {
		IBreakpoint bp = mock(IBreakpoint.class);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertEquals(BreakpointType.UNDEFINED, result.getBreakpointType());
	}

	@Test
	public void testCreateBreakpointEnabled() throws CoreException {
		IBreakpoint bp = mock(IBreakpoint.class);
		when(bp.isEnabled()).thenReturn(true);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertTrue(result.isEnabled());
	}

	@Test
	public void testCreateBreakpointDisabled() throws CoreException {
		IBreakpoint bp = mock(IBreakpoint.class);
		when(bp.isEnabled()).thenReturn(false);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertFalse(result.isEnabled());
	}

	@Test
	public void testCreateBreakpointHitCount() {
		IBreakpoint bp = mock(IBreakpoint.class);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertEquals(-1, result.getHitCount());
	}

	@Test
	public void testCreateJavaBreakpointNoHitCount() throws CoreException {
		IJavaBreakpoint bp = mock(IJavaBreakpoint.class);
		when(bp.getHitCount()).thenReturn(-1);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertEquals(-1, result.getHitCount());
	}

	@Test
	public void testCreateJavaBreakpointHitCount() throws CoreException {
		IJavaBreakpoint bp = mock(IJavaBreakpoint.class);
		when(bp.getHitCount()).thenReturn(2);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertEquals(2, result.getHitCount());
	}

	@Test
	public void testCreateJavaBreakpointSuspendPolicy() throws CoreException {
		IJavaBreakpoint bp = mock(IJavaBreakpoint.class);
		when(bp.getSuspendPolicy()).thenReturn(1);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertEquals(1, result.getSuspendPolicy());
	}

	@Test
	public void testCreateJavaLineBreakpointConditionEnabled() throws CoreException {
		IJavaLineBreakpoint bp = mock(IJavaLineBreakpoint.class);
		when(bp.isConditionEnabled()).thenReturn(true);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertTrue(result.isConditionEnabled());
	}

	@Test
	public void testCreateJavaLineBreakpointConditionDisabled() throws CoreException {
		IJavaLineBreakpoint bp = mock(IJavaLineBreakpoint.class);
		when(bp.isConditionEnabled()).thenReturn(false);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertFalse(result.isConditionEnabled());
	}

	@Test
	public void testCreateJavaLineBreakpointNoCondition() throws CoreException {
		IJavaLineBreakpoint bp = mock(IJavaLineBreakpoint.class);
		when(bp.getCondition()).thenReturn(null);
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertNull(result.getCondition());
	}

	@Test
	public void testCreateJavaLineBreakpointCondition() throws CoreException {
		IJavaLineBreakpoint bp = mock(IJavaLineBreakpoint.class);
		when(bp.getCondition()).thenReturn("cond");
		Breakpoint result = BreakpointCreator.createBreakpoint(bp);
		assertEquals("cond", result.getCondition());
	}

}
