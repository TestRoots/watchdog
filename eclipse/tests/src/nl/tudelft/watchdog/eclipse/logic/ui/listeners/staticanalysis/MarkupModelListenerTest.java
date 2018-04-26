package nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.FileWarningSnapshotEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.StaticAnalysisWarningEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType.WatchDogEventEditorSpecificImplementation;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.WorkbenchListener;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis.EclipseMarkupModelListener;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class MarkupModelListenerTest {

    // We actually expect message 388 to be here, however as pointed out in
    // https://github.com/eclipse/eclipse.jdt.core/blob/efc9b650d8590a5670b5897ab6f8c0fb0db2799d/org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/problem/DefaultProblemFactory.java#L111-L113
    // all keys are offset by 1. Therefore 389 is the expected assert value
    private static final String PRE_EXISTING_MARKER_CLASSIFICATION_TYPE = "389";

    private WorkbenchListener workbenchListener;
    private TrackingEventManager trackingEventManager;
    private TransferManager transferManager;
    private IWorkspace workspace;
    private IProject project;
    private IFile testFile;
    private IFile preExistingTestFile;
    private IMarker preExistingMarker;
    private IMarker preExistingMarker2;

    private List<TrackingEventType> generatedEvents;

    @Before
    public void setup() throws Exception {
        WatchDogEventType.intervalManager = Mockito.mock(IntervalManager.class);
        WatchDogEventType.editorSpecificImplementation = Mockito.mock(WatchDogEventEditorSpecificImplementation.class);
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
        this.preExistingMarker.setAttribute(IMarker.MESSAGE, "The import java.util.Set is never used");
        this.preExistingMarker.setAttribute(IMarker.LINE_NUMBER, 2);
        this.preExistingMarker2 = this.preExistingTestFile.createMarker(IMarker.PROBLEM);
        this.preExistingMarker2.setAttribute(IMarker.LINE_NUMBER, 5);

        this.saveWorkspaceAndWaitForBuild();
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

    private void saveWorkspaceAndWaitForBuild() throws Exception {
        this.workspace.save(true, null);
        Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
    }

    @After
    public void teardown() throws Exception {
        this.project.delete(true, true, null);
        this.saveWorkspaceAndWaitForBuild();
        this.workspace.removeResourceChangeListener(this.workbenchListener.getMarkupModelListener());
    }

    @Test
    public void picks_up_created_warnings_on_first_save() throws Exception {
        IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
        marker.setAttribute(IMarker.LINE_NUMBER, 1);
        IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
        marker2.setAttribute(IMarker.LINE_NUMBER, 2);

        this.saveWorkspaceAndWaitForBuild();

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

        this.saveWorkspaceAndWaitForBuild();

        this.testFile.appendContents(generateFileStreamLines(10), true, true, null);

        this.saveWorkspaceAndWaitForBuild();

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

        this.saveWorkspaceAndWaitForBuild();

        this.testFile.appendContents(generateFileStreamLines(10), true, true, null);
        marker.delete();

        this.saveWorkspaceAndWaitForBuild();

        Assert.assertArrayEquals(generatedEvents.stream().toArray(),
                new TrackingEventType[] {TrackingEventType.SA_WARNING_CREATED, TrackingEventType.SA_WARNING_CREATED, TrackingEventType.SA_WARNING_REMOVED});
    }

    @Test
    public void keeps_track_of_marker_deletions_based_on_message() throws Exception {
        IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
        marker.setAttribute(IMarker.MESSAGE, "Unused import java.util.*;");
        IMarker marker2 = this.testFile.createMarker(IMarker.PROBLEM);
        marker2.setAttribute(IMarker.MESSAGE, "Unused import java.*;");

        this.saveWorkspaceAndWaitForBuild();

        this.testFile.appendContents(generateFileStreamLines(10), true, true, null);

        marker2.delete();
        IMarker marker3 = this.testFile.createMarker(IMarker.PROBLEM);
        marker3.setAttribute(IMarker.MESSAGE, "Unused import java.*;");

        this.saveWorkspaceAndWaitForBuild();

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

        this.saveWorkspaceAndWaitForBuild();

        marker3.delete();

        IMarker marker5 = this.testFile.createMarker(IMarker.PROBLEM);
        marker5.setAttribute(IMarker.MESSAGE, "Unused import java.time.*;");
        marker5.setAttribute(IMarker.LINE_NUMBER, 5);

        IMarker marker3Replaced = this.testFile.createMarker(IMarker.PROBLEM);
        marker3Replaced.setAttribute(IMarker.MESSAGE, "Unused import java.util.*;");
        marker3Replaced.setAttribute(IMarker.LINE_NUMBER, 3);

        this.saveWorkspaceAndWaitForBuild();

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

        this.saveWorkspaceAndWaitForBuild();

        assertEquals(generatedEvents.size(), 2);
        assertTrue(generatedEvents.stream()
                .allMatch(TrackingEventType.SA_WARNING_CREATED::equals));
    }

    @Test
    public void can_delete_warning_existed_before_file_modified() throws Exception {
        this.preExistingMarker.delete();

        this.saveWorkspaceAndWaitForBuild();

        assertEquals(generatedEvents.size(), 1);
        assertTrue(generatedEvents.stream()
                .allMatch(TrackingEventType.SA_WARNING_REMOVED::equals));
    }

    @Test
    public void correctly_generates_document_information() throws Exception {
        List<StaticAnalysisWarningEvent> generatedWarnings = this.deleteMarkerAndReturnGeneratedWarningList(this.preExistingMarker);

        assertEquals(generatedWarnings.size(), 1);

        Document document = generatedWarnings.get(0).document;
        assertEquals(document.getFileName(), "Existing.java");
        assertEquals(document.getContent().split("\\d+").length, 25);
    }

    @Test
    public void correctly_classifies_warning_type_for_existing_marker() throws Exception {
        List<StaticAnalysisWarningEvent> generatedWarnings = this.deleteMarkerAndReturnGeneratedWarningList(this.preExistingMarker);

        assertEquals(generatedWarnings.size(), 1);

        assertEquals(generatedWarnings.get(0).warning.type, PRE_EXISTING_MARKER_CLASSIFICATION_TYPE);
    }

    @Test
    public void correctly_classifies_warning_type_for_creating_a_new_marker() throws Exception {
        List<StaticAnalysisWarningEvent> generatedEvents = process_marker_and_return_generated_warning_list(() -> {
            try {
                IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.MESSAGE, "Duplicate tag for parameter");
            } catch (CoreException e) {
                e.printStackTrace();
            }
        });

        assertEquals(generatedEvents.size(), 1);

        // See explanation in the previous test why this is 474
        assertEquals(generatedEvents.get(0).warning.type, "474");
    }

    @Test
    public void correctly_classifies_checkstyle_warning_type() throws Exception {
        List<StaticAnalysisWarningEvent> generatedEvents = process_marker_and_return_generated_warning_list(() -> {
            try {
                IMarker marker = this.testFile.createMarker(EclipseMarkupModelListener.CHECKSTYLE_MARKER_ID);
                marker.setAttribute(IMarker.MESSAGE, "Using the '.*' form of import should be avoided - java.util.*.");
            } catch (CoreException e) {
                e.printStackTrace();
            }
        });

        assertEquals(generatedEvents.size(), 1);
        assertEquals(generatedEvents.get(0).warning.type, "checkstyle.imports.import.avoidStar");
    }

    @Test
    public void non_existing_warning_should_not_match_any_type() throws Exception {
        List<StaticAnalysisWarningEvent> generatedEvents = process_marker_and_return_generated_warning_list(() -> {
            try {
                IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.MESSAGE, "This warning does not exist");
            } catch (CoreException e) {
                e.printStackTrace();
            }
        });

        assertEquals(generatedEvents.size(), 1);
        assertEquals(generatedEvents.get(0).warning.type, "unknown");
    }

    @Test
    public void sets_line_number_for_generated_warning() throws Exception {
        List<StaticAnalysisWarningEvent> generatedEvents = process_marker_and_return_generated_warning_list(() -> {
            try {
                IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.MESSAGE, "This warning does not exist");
                marker.setAttribute(IMarker.LINE_NUMBER, 15);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        });

        assertEquals(generatedEvents.size(), 1);
        assertEquals(generatedEvents.get(0).warning.lineNumber, 15);
    }

    @Test
    public void computes_warning_time_difference_for_removed_warning() throws Exception {
        List<StaticAnalysisWarningEvent> generatedEvents = process_marker_and_return_generated_warning_list(() -> {
            try {
                IMarker marker = this.testFile.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.MESSAGE, "This warning does not exist");
                marker.setAttribute(IMarker.LINE_NUMBER, 15);

                this.saveWorkspaceAndWaitForBuild();

                marker.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertEquals(generatedEvents.size(), 2);
        // We simply want any time difference here. If there is any, it is not -1, but 0, 1 or anything above
        // We do not want to rely on timing differences to assert on specific values, so asserting we have
        // anything is sufficient
        assertNotEquals(generatedEvents.get(1).warning.secondsBetween, -1);
    }

    @Test
    public void generates_file_warning_snapshot_with_correct_line_numbers_when_opening_a_file() throws Exception {
        runForActivePage((page) -> {
            IEditorPart editor;
            try {
                editor = IDE.openEditor(page, this.preExistingTestFile);
            } catch (PartInitException e) {
                throw new RuntimeException(e);
            }

            ArgumentCaptor<FileWarningSnapshotEvent> captor = ArgumentCaptor.forClass(FileWarningSnapshotEvent.class);
            Mockito.verify(this.trackingEventManager).addEvent(captor.capture());

            try {
                int[] lineNumbers = captor.getValue().warnings.stream()
                        .map(warning -> warning.lineNumber)
                        .mapToInt(Integer::intValue)
                        .toArray();

                assertArrayEquals(lineNumbers, new int[] {2, 5});
            } finally {
                page.closeEditor(editor, false);
            }
        });
    }

    @Test
    public void generates_file_warning_snapshot_with_correct_classification_when_opening_a_file() throws Exception {
        runForActivePage((page) -> {
            IEditorPart editor;
            try {
                editor = IDE.openEditor(page, this.preExistingTestFile);
            } catch (PartInitException e) {
                throw new RuntimeException(e);
            }

            ArgumentCaptor<FileWarningSnapshotEvent> captor = ArgumentCaptor.forClass(FileWarningSnapshotEvent.class);
            Mockito.verify(this.trackingEventManager).addEvent(captor.capture());

            try {
                String[] classifications = captor.getValue().warnings.stream()
                        .map(warning -> warning.type)
                        .toArray(String[]::new);

                assertArrayEquals(classifications, new String[] {PRE_EXISTING_MARKER_CLASSIFICATION_TYPE, "unknown"});
            } finally {
                page.closeEditor(editor, false);
            }
        });
    }

    private void runForActivePage(Consumer<IWorkbenchPage> consumer) {
        Display.getDefault().syncExec(() -> {
            consumer.accept(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
        });
    }

	private List<StaticAnalysisWarningEvent> process_marker_and_return_generated_warning_list(Runnable runnable) throws Exception {
		List<StaticAnalysisWarningEvent> list = new ArrayList<>();

		Mockito.doAnswer(new Answer<Object>() {

			@SuppressWarnings("unchecked")
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Stream<StaticAnalysisWarningEvent> stream = (Stream<StaticAnalysisWarningEvent>) invocation.getArguments()[0];
				list.addAll(stream.collect(Collectors.toList()));
				return null;
			}}).when(this.trackingEventManager).addEvents(Mockito.any());

		runnable.run();

		this.saveWorkspaceAndWaitForBuild();

		return list;
	}

    private List<StaticAnalysisWarningEvent> deleteMarkerAndReturnGeneratedWarningList(IMarker marker) throws Exception {
        return process_marker_and_return_generated_warning_list(() -> {
            try {
                marker.delete();
            } catch (CoreException e) {
                e.printStackTrace();
            }
        });
    }

}
