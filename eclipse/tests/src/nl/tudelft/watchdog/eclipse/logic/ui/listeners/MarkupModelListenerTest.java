package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.WorkbenchListener;

import static org.junit.Assert.assertTrue;

public class MarkupModelListenerTest {
	
	private WorkbenchListener workbenchListener;
	private TrackingEventManager trackingEventManager;
	private TransferManager transferManager;
	private EclipseMarkupModelListener markupModelListener;
	private IWorkspace workspace;
	private IProject project;
	private IFile testFile;

	@Before
	public void setup() throws Exception {
		WatchDogEventType.intervalManager = Mockito.mock(IntervalManager.class);
		this.trackingEventManager = Mockito.mock(TrackingEventManager.class);
		this.transferManager = Mockito.mock(TransferManager.class);
		
		this.workbenchListener = Mockito.spy(new WorkbenchListener(trackingEventManager, transferManager));
		this.markupModelListener = new EclipseMarkupModelListener(this.trackingEventManager);
		
		Mockito.when(this.workbenchListener.createMarkupModelListener()).thenReturn(markupModelListener);
		
		this.setUpTestingProject();
	}
	
	private void setUpTestingProject() throws Exception {
		this.workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceDescription description = this.workspace.getDescription();
		description.setAutoBuilding(true);
		this.workspace.setDescription(description);
		
		this.project = this.workspace.getRoot().getProject("Testing");
		this.project.create(this.workspace.newProjectDescription(project.getName()), null);
		this.project.open(null);
		
		IFolder folder = project.getFolder("src");
		folder.create(true, true, null);
		this.testFile = folder.getFile("Main.java");
		
		this.testFile.create(generateFileStreamLines(25), true, null);
	}
	
	private InputStream generateFileStreamLines(int numLines) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < numLines; i++) {
			builder.append("Line ")
				   .append(i)
				   .append('\n');
		}
		
		return new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));
	}

	@After
	public void tearDown() throws CoreException {
		this.project.delete(true, true, null);
		this.workspace.save(true, null);
	}
	
	@Test
	public void picks_up_created_warnings_on_first_save() throws Exception {
		IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.LINE_NUMBER, 1);
		IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
		marker2.setAttribute(IMarker.LINE_NUMBER, 2);
		this.workspace.save(true, null);
		
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		
		ArgumentCaptor<StaticAnalysisWarningEvent> captor = ArgumentCaptor.forClass(StaticAnalysisWarningEvent.class);
		Mockito.verify(this.trackingEventManager, Mockito.times(2)).addEvent(captor.capture());
		assertTrue(captor.getAllValues().stream()
				.allMatch(event -> event.getType() == TrackingEventType.SA_WARNING_CREATED));
	}
	
	@Test
	public void does_not_generate_events_for_same_markers() throws Exception {
		IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.LINE_NUMBER, 1);
		IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
		marker2.setAttribute(IMarker.LINE_NUMBER, 2);
		
		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		
		this.testFile.appendContents(generateFileStreamLines(10), true, true, null);
		
		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		
		ArgumentCaptor<StaticAnalysisWarningEvent> captor = ArgumentCaptor.forClass(StaticAnalysisWarningEvent.class);
		Mockito.verify(this.trackingEventManager, Mockito.times(2)).addEvent(captor.capture());
		assertTrue(captor.getAllValues().stream()
				.allMatch(event -> event.getType() == TrackingEventType.SA_WARNING_CREATED));
	}
	
	@Test
	public void generates_removal_after_marker_is_deleted() throws Exception {
		IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.LINE_NUMBER, 1);
		IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
		marker2.setAttribute(IMarker.LINE_NUMBER, 2);
		
		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		
		this.testFile.appendContents(generateFileStreamLines(10), true, true, null);
		marker.delete();
		
		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		
		ArgumentCaptor<StaticAnalysisWarningEvent> captor = ArgumentCaptor.forClass(StaticAnalysisWarningEvent.class);
		Mockito.verify(this.trackingEventManager, Mockito.times(3)).addEvent(captor.capture());
		Assert.assertArrayEquals(captor.getAllValues().stream().map(StaticAnalysisWarningEvent::getType).toArray(),
				new TrackingEventType[] {TrackingEventType.SA_WARNING_CREATED, TrackingEventType.SA_WARNING_CREATED, TrackingEventType.SA_WARNING_REMOVED});
	}
	
	@Test
	public void keeps_track_of_marker_deletions_based_on_message() throws Exception {
		IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.MESSAGE, "Unused import java.util.*;");
		IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
		marker2.setAttribute(IMarker.MESSAGE, "Unused import java.*;");
		
		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		
		this.testFile.appendContents(generateFileStreamLines(10), true, true, null);
		marker2.delete();
		IMarker marker3 = this.testFile.createMarker(IMarker.PROBLEM);
		marker3.setAttribute(IMarker.MESSAGE, "Unused import java.*;");
		
		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		
		ArgumentCaptor<StaticAnalysisWarningEvent> captor = ArgumentCaptor.forClass(StaticAnalysisWarningEvent.class);
		Mockito.verify(this.trackingEventManager, Mockito.times(2)).addEvent(captor.capture());
		Assert.assertArrayEquals(captor.getAllValues().stream().map(StaticAnalysisWarningEvent::getType).toArray(),
				new TrackingEventType[] {TrackingEventType.SA_WARNING_CREATED, TrackingEventType.SA_WARNING_CREATED});
	}

}
