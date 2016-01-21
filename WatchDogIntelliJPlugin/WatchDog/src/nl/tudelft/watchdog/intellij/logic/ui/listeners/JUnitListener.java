package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.execution.Location;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestStatusListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.search.GlobalSearchScope;
import nl.tudelft.watchdog.intellij.logic.IntervalInitializationManager;
import nl.tudelft.watchdog.intellij.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.JUnitEvent;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

/**
 * A listener to the execution of Junit test via the IDE.
 */
public class JUnitListener extends TestStatusListener {

    @Override
    public void testSuiteFinished(AbstractTestProxy testProxy) {
        Project project = getProject(testProxy);
        if (!WatchDogUtils.isWatchDogActive(project)) {
            return;
        }

        WatchDogEventManager eventManager = IntervalInitializationManager.getInstance(project.getName()).getEventManager();
        JUnitInterval interval = new JUnitInterval(testProxy);
        eventManager.update(new JUnitEvent(interval));
    }

    /** For given AbstractTestProxy returns the Project the test belongs to. Should always return Project, never null. */
    public static Project getProject(AbstractTestProxy test) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project openedProject : openProjects) {
            Location location = getProjectLocation(test, openedProject);

            if (location != null) {
                return openedProject;
            }

        }
        return null;
    }

    /** IntelliJ14 and IntelliJ15 compatible method for returning the location of a project */
    private static Location getProjectLocation(AbstractTestProxy test, Project openedProject) {
        // IntelliJ15 compatible call
        // Location is an IntelliJ-representation of a file location in their VirtualFileSystem.
        // In this call, we ask for a location of current test within each of the opened projects.
        // The location is different from null for exactly the one project it belongs to.
        Location location = getFirstLeaf(test).getLocation(openedProject, GlobalSearchScope.allScope(openedProject));
        if(location == null) {
            // IntelliJ14 compatible call
            location = test.getLocation(openedProject, GlobalSearchScope.allScope(openedProject));
        }

        return location;
    }

    private static AbstractTestProxy getFirstLeaf(AbstractTestProxy testProxy) {
        if(testProxy.isLeaf()) {
            return testProxy;
        }
        return getFirstLeaf(testProxy.getChildren().get(0));
    }
}
