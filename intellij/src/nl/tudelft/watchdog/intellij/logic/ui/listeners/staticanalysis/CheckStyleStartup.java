package nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import org.infernus.idea.checkstyle.CheckStylePlugin;
import org.infernus.idea.checkstyle.CheckstyleClassLoader;
import org.infernus.idea.checkstyle.CheckstyleProjectService;
import org.infernus.idea.checkstyle.model.ConfigurationLocation;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CheckStyleChecksMessagesFetcher.addCheckStyleMessagesToBundle;
import static nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CheckStyleChecksMessagesFetcher.addMessageToCheckstyleBundle;

public class CheckStyleStartup implements ProjectComponent {

    private final Project project;

    public CheckStyleStartup(@NotNull final Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
        try {
            CheckstyleProjectService service = ServiceManager.getService(this.project, CheckstyleProjectService.class);

            // This loader can load all checks as defined in CheckStyle
            final ClassLoader checkStylePackageLoaderClassLoader = service.getCheckstyleInstance().getClass().getClassLoader();

            // This loader is used to obtain the actual resources
            final ClassLoader checkStylePluginClassLoader = getPluginCreatedClassLoaderFromService(service);

            addCheckStyleMessagesToBundle(StaticAnalysisMessageClassifier.CHECKSTYLE_BUNDLE, checkStylePackageLoaderClassLoader, checkStylePluginClassLoader);
            addMessagesForActiveConfiguration(service);

            StaticAnalysisMessageClassifier.CHECKSTYLE_BUNDLE.sortList();
        } catch (Exception e) {
            WatchDogLogger.getInstance().logSevere("Could not initialize the CheckStyle plugin. This is likely an issue with an outdated version of the CheckStyle plugin: " + e);
        }
    }

    // TODO(timvdlippe): update the messages when the configuration changes
    private void addMessagesForActiveConfiguration(CheckstyleProjectService service) {
        final CheckStylePlugin checkStylePlugin = project.getComponent(CheckStylePlugin.class);
        final ConfigurationLocation activeConfigLocation = checkStylePlugin.configurationManager().getCurrent().getActiveLocation();

        if (activeConfigLocation == null) {
            return;
        }

        service.getCheckstyleInstance().peruseConfiguration(
                service.getCheckstyleInstance()
                        .loadConfiguration(activeConfigLocation, true, new HashMap<>()),
                module -> {
                    String moduleKey = activeConfigLocation.getDescription() + "." + module.getName() + ".";

                    for (Map.Entry<String, String> message: module.getMessages().entrySet()) {
                        addMessageToCheckstyleBundle(StaticAnalysisMessageClassifier.CHECKSTYLE_BUNDLE,moduleKey + message.getKey(), message.getValue());
                    }
                }
        );
    }

    // A bunch of reflection magic, as the plugin does not expose its internal classLoader
    // TODO(timvdlippe): Update this code to use the type-safe methods once a PR to the plugin is merged that implements the method.
    private ClassLoader getPluginCreatedClassLoaderFromService(CheckstyleProjectService service) throws NoSuchFieldException, IllegalAccessException {
        Field checkstyleClassLoaderField = service.getClass().getDeclaredField("checkstyleClassLoader");
        checkstyleClassLoaderField.setAccessible(true);

        CheckstyleClassLoader loader = ((CheckstyleClassLoader) checkstyleClassLoaderField.get(service));

        Field classLoaderField = loader.getClass().getDeclaredField("classLoader");
        classLoaderField.setAccessible(true);

        return (ClassLoader) classLoaderField.get(loader);
    }

}
