package nl.tudelft.watchdog.ui;

import com.intellij.openapi.project.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class WatchDogToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        final WatchDogView watchDogView = new WatchDogView(false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(watchDogView,"",false);
        toolWindow.setAvailable(true,null);
        toolWindow.setToHideOnEmptyContent(true);
        toolWindow.getContentManager().addContent(content);
    }

}
