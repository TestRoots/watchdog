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

/** A listener to the execution of Junit test via the IDE. */
public class JUnitListener extends TestStatusListener {

    @Override
    public void testSuiteFinished(AbstractTestProxy abstractTestProxy) {
        if(!WatchDogUtils.isWatchDogActive(getProject(abstractTestProxy))) {
            return;
        }
        EventManager eventManager = InitializationManager.getInstance(getProject(abstractTestProxy).getName()).getEventManager();
        JUnitInterval interval = new JUnitInterval(abstractTestProxy);
        eventManager.update(new JUnitEvent(interval));
    }

    private Project getProject(AbstractTestProxy test) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project openedProject : openProjects) {
            Location location = test.getLocation(openedProject, GlobalSearchScope.allScope(openedProject));

            if (location != null) {
                return openedProject;
            }
        }
        return null;
    }
}
