package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.WorkbenchListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MarkupModelListenerTest {

	private WorkbenchListener workbenchListener;
	private TrackingEventManager trackingEventManager;
	private TransferManager transferManager;
	private IWorkspace workspace;
	private IProject project;
	private IFile testFile;
	private IFile preExistingTestFile;
	private IMarker preExistingMarker;

	private List<TrackingEventType> generatedEvents;

	@Before
	public void setup() throws Exception {
		WatchDogEventType.intervalManager = Mockito.mock(IntervalManager.class);
		this.transferManager = Mockito.mock(TransferManager.class);
		this.trackingEventManager = Mockito.mock(TrackingEventManager.class);

		this.generatedEvents = new ArrayList<>();

		Mockito.doAnswer(new Answer<Object>() {

			@SuppressWarnings("unchecked")
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Stream<StaticAnalysisWarningEvent> stream = (Stream<StaticAnalysisWarningEvent>) invocation.getArguments()[0];
				stream.map(StaticAnalysisWarningEvent::getType).forEach(generatedEvents::add);
				return null;
			}}).when(this.trackingEventManager).addEvents(Mockito.any());

		this.setUpTestingProject();

		this.workbenchListener = new WorkbenchListener(trackingEventManager, transferManager);
		this.workbenchListener.attachListeners();
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

		this.preExistingTestFile = folder.getFile("Existing.java");
		this.preExistingTestFile.create(generateFileStreamLines(25), true, null);
		this.preExistingMarker = this.preExistingTestFile.createMarker(IMarker.PROBLEM);
		this.preExistingTestFile.createMarker(IMarker.PROBLEM);

		this.workspace.save(true, null);

		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
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
	public void tearDown() throws Exception {
		this.project.delete(true, true, null);
		this.workspace.save(true, null);

		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		this.workbenchListener.shutDown();
	}

	@Test
	public void picks_up_created_warnings_on_first_save() throws Exception {
		IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.LINE_NUMBER, 1);
		IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
		marker2.setAttribute(IMarker.LINE_NUMBER, 2);
		this.workspace.save(true, null);

		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);

		assertEquals(generatedEvents.size(), 2);
		assertTrue(generatedEvents.stream()
				.allMatch(TrackingEventType.SA_WARNING_CREATED::equals));
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

		assertEquals(generatedEvents.size(), 2);
		assertTrue(generatedEvents.stream()
				.allMatch(TrackingEventType.SA_WARNING_CREATED::equals));
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

		Assert.assertArrayEquals(generatedEvents.stream().toArray(),
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

		Assert.assertArrayEquals(generatedEvents.stream().toArray(),
				new TrackingEventType[] {TrackingEventType.SA_WARNING_CREATED, TrackingEventType.SA_WARNING_CREATED});
	}

	@Test
	public void diffing_algorithm_works_on_sorted_lists_by_line_number() throws Exception {
		IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.MESSAGE, "Unused import java.util.*;");
		marker.setAttribute(IMarker.LINE_NUMBER, 1);
		IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
		marker2.setAttribute(IMarker.MESSAGE, "Unused import java.*;");
		marker2.setAttribute(IMarker.LINE_NUMBER, 2);
		IMarker marker3 = this.testFile.createMarker(IMarker.PROBLEM);
		marker3.setAttribute(IMarker.MESSAGE, "Unused import java.util.*;");
		marker3.setAttribute(IMarker.LINE_NUMBER, 3);
		IMarker marker4 = this.testFile.createMarker(IMarker.PROBLEM);
		marker4.setAttribute(IMarker.MESSAGE, "Unused import java.*;");
		marker4.setAttribute(IMarker.LINE_NUMBER, 4);

		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);

		marker3.delete();

		IMarker marker5 = this.testFile.createMarker(IMarker.PROBLEM);
		marker5.setAttribute(IMarker.MESSAGE, "Unused import java.time.*;");
		marker5.setAttribute(IMarker.LINE_NUMBER, 5);

		IMarker marker3Replaced = this.testFile.createMarker(IMarker.PROBLEM);
		marker3Replaced.setAttribute(IMarker.MESSAGE, "Unused import java.util.*;");
		marker3Replaced.setAttribute(IMarker.LINE_NUMBER, 3);

		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);

		assertEquals(generatedEvents.size(), 5);
		assertTrue(generatedEvents.stream()
				.allMatch(TrackingEventType.SA_WARNING_CREATED::equals));
	}

	@Test
	public void modifying_an_existing_file_only_triggers_creations_for_new_warnings() throws Exception {
		IMarker marker = this.preExistingTestFile.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.MESSAGE, "Unused import java.util.*;");
		IMarker marker2 = this.preExistingTestFile.createMarker(IMarker.PROBLEM);
		marker2.setAttribute(IMarker.MESSAGE, "Unused import java.*;");

		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);

		assertEquals(generatedEvents.size(), 2);
		assertTrue(generatedEvents.stream()
				.allMatch(TrackingEventType.SA_WARNING_CREATED::equals));
	}

	@Test
	public void can_delete_warning_existed_before_file_modified() throws Exception {
		this.preExistingMarker.delete();

		this.workspace.save(true, null);
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);

		assertEquals(generatedEvents.size(), 1);
		assertTrue(generatedEvents.stream()
				.allMatch(TrackingEventType.SA_WARNING_REMOVED::equals));
	}

}
