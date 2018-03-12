package nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;
import org.infernus.idea.checkstyle.CheckStylePlugin;
import org.infernus.idea.checkstyle.CheckstyleClassLoader;
import org.infernus.idea.checkstyle.CheckstyleProjectService;
import org.infernus.idea.checkstyle.model.ConfigurationLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

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
            final Map<String, String> nameToModuleName = getModuleMapFromPackageObjectFactory(checkStylePackageLoaderClassLoader);

            // This loader is used to obtain the actual resources
            final ClassLoader checkStylePluginClassLoader = getPluginCreatedClassLoaderFromService(service);

            addMessagesToCheckStyleBundleForLoadedChecks(checkStylePackageLoaderClassLoader, nameToModuleName, checkStylePluginClassLoader);
            addMessagesForActiveConfiguration(service);

            StaticAnalysisWarningClassifier.CHECKSTYLE_BUNDLE.sortList();
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
                        this.addMessageToCheckstyleBundle(moduleKey + message.getKey(), message.getValue());
                    }
                }
        );
    }

    private void addMessagesToCheckStyleBundleForLoadedChecks(ClassLoader checkStylePackageLoaderClassLoader,
                                                              Map<String, String> nameToModuleName,
                                                              ClassLoader checkStylePluginClassLoader) throws IOException {
        // Use a map to have distinct resource properties, to make sure we don't process messages double
        Map<String, String> resourceToPackage = new HashMap<>();

        nameToModuleName.values().stream()
                .map(WatchDogUtils.unchecked(checkStylePackageLoaderClassLoader::loadClass))
                // In case loading of the class failed, we have to filter the null value
                .filter(Objects::nonNull)
                .forEach(clazz -> {
                    String packageName = clazz.getPackage().getName();
                    String resourceLocation = packageName.replaceAll("\\.", "/") + "/messages.properties";
                    resourceToPackage.put(resourceLocation, packageName.substring(packageName.lastIndexOf(".") + 1));
                });

        for (Map.Entry<String, String> entry : resourceToPackage.entrySet()) {
            Properties properties = new Properties();
            try (InputStream stream = checkStylePluginClassLoader.getResourceAsStream(entry.getKey())) {
                if (stream != null) {
                    properties.load(stream);
                }
            }

            for (String name : properties.stringPropertyNames()) {
                addMessageToCheckstyleBundle(entry.getValue() + "." + name, properties.getProperty(name));
            }
        }
    }

    private void addMessageToCheckstyleBundle(String name, String message) {
        try {
            StaticAnalysisWarningClassifier.CHECKSTYLE_BUNDLE.addMessage(
                    "checkstyle." + name,
                    message
            );
        } catch (Exception ignored) {
            WatchDogLogger.getInstance().logSevere("Could not create CheckStyle pattern for key \"" + name + "\"");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getModuleMapFromPackageObjectFactory(ClassLoader checkStylePackageLoaderClassLoader)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {

        final Class<?> packageObjectFactoryClass = checkStylePackageLoaderClassLoader
                .loadClass("com.puppycrawl.tools.checkstyle.PackageObjectFactory");

        final Field packageObjectFactoryMapField = packageObjectFactoryClass.getDeclaredField("NAME_TO_FULL_MODULE_NAME");
        packageObjectFactoryMapField.setAccessible(true);

        return (Map<String, String>) packageObjectFactoryMapField.get(null);
    }

    private ClassLoader getPluginCreatedClassLoaderFromService(CheckstyleProjectService service) throws NoSuchFieldException, IllegalAccessException {
        Field checkstyleClassLoaderField = service.getClass().getDeclaredField("checkstyleClassLoader");
        checkstyleClassLoaderField.setAccessible(true);

        CheckstyleClassLoader loader = ((CheckstyleClassLoader) checkstyleClassLoaderField.get(service));

        Field classLoaderField = loader.getClass().getDeclaredField("classLoader");
        classLoaderField.setAccessible(true);

        return (ClassLoader) classLoaderField.get(loader);
    }

}
