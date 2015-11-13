package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.execution.Location;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestStatusListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.search.GlobalSearchScope;
import nl.tudelft.watchdog.intellij.logic.InitializationManager;
import nl.tudelft.watchdog.intellij.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.intellij.logic.ui.EventManager;
import nl.tudelft.watchdog.core.logic.ui.events.JUnitEvent;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

/**
 * A listener to the execution of Junit test via the IDE.
 */
public class JUnitListener extends TestStatusListener {

    @Override
    public void testSuiteFinished(AbstractTestProxy testProxy) {
        if (!WatchDogUtils.isWatchDogActive(getProject(testProxy))) {
            return;
        }

        EventManager eventManager = InitializationManager.getInstance(getProject(testProxy).getName()).getEventManager();
        JUnitInterval interval = new JUnitInterval(testProxy);
        eventManager.update(new JUnitEvent(interval));
    }

    /** For given AbstractTestProxy returns the Project the test belongs to. Should always return Project, never null. */
    private Project getProject(AbstractTestProxy test) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project openedProject : openProjects) {
            // Location is a part of IntelliJ API which is used in their representation of VirtualFileSystem.
            // In this call, we ask for a location of current test within each of the opened projects.
            // The location is different from null for exactly the one project it belongs to.
            Location location = test.getLocation(openedProject, GlobalSearchScope.allScope(openedProject));

            if (location != null) {
                return openedProject;
            }
        }
        return null;
    }
}
