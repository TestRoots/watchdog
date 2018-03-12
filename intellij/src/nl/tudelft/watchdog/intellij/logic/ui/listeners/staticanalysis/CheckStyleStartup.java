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

/**
 * Hello and welcome to this class full of reflection, hacks and other shenanigans.
 * Before you ask: yes this is necessary. To explain why, please sit back, relax (while you still can)
 * and get to know the absolute horror of having multiple classloaders.
 *
 *
 *
 * To start our journey, we first have to understand how CheckStyle is loaded in IntelliJ.
 * While normally the library you depend on is available on the classpath, the CheckStyle-IDEA plugin
 * does not have that. The reason is valid: the plugin wants to dynamically load different versions
 * of CheckStyle. This means that the plugin has to dynamically load jars, and yes, that is done with
 * a {@link ClassLoader}.
 *
 * Now the real magic starts when you realize that there are multiple classloaders necessary to load
 * the correct files. The first classloader is created in the CheckStyle plugin. This plugin is configured
 * to load several jars, including the CheckStyle jar. This loader therefore has access to CheckStyle
 * classes. In the code below, this loader is called "checkStylePluginClassLoader".
 *
 * Besides the actual classes, other configuration must also be loaded. This configuration however partly
 * resides in CheckStyle and other files reside in the plugin. To that end, the plugin implements a so-called
 * "csaccess" layer, which lives besides CheckStyle. This layer has its own classloader and has access to:
 * 1. the csaccess files as implemented by the plugin and 2. the resources available in CheckStyle.
 * This loader is named "checkStylePackageLoaderClassLoader" in the code below.
 *
 * Besides these two classloader, this class itself lives in its own classloading context, which does have
 * access to several "non-csaccess" files. This mostly includes {@link CheckstyleProjectService} and
 * {@link CheckStylePlugin}. These classes are implemented in the plugin and handle, for example, the
 * loading of configuration files defined by the user.
 *
 *
 *
 * The overall logic of implementation is therefore as follows: Load the configuration and other plugin-relevant\
 * classes in our current classloader. Once we got the relevant classes, obtain the classloader created by
 * the plugin (see {@link #getPluginCreatedClassLoaderFromService(CheckstyleProjectService)}) and load the
 * CheckStyle checks (as defined in "com.puppycrawl.tools.checkstyle.checks"). Once we got all checks,
 * we have to obtain their corresponding messages.
 *
 * Since these messages are stored in resources named "messages.properties" and based on the packagename,
 * we need to use the last classloader to fetch the resources from there. We process the resources in
 * {@link #addMessagesToCheckStyleBundleForLoadedChecks(ClassLoader, Map, ClassLoader)}.
 * Observe that this method takes 2 classloaders, as we need both the classes (to obtain the resource path)
 * and the other one to obtain the actual resource.
 *
 * Lastly, we also need to process the configuration files, as some messages are actually configured there.
 * The plugin ships with two core configurations (sun and google), which define these messages.
 * The logic for that is implemented in {@link #addMessagesForActiveConfiguration(CheckstyleProjectService)}.
 *
 *
 *
 * All in all, this journey took a long time to figure out. If you need to update this code, think carefully
 * about which classloader can load what. When I originally wrote this class, it was mostly based on
 * {@see https://github.com/jshiell/checkstyle-idea/blob/master/src/csaccess/java/org/infernus/idea/checkstyle/service/cmd/OpCreateChecker.java}
 * This class contained crucial documentation regarding its "moduleClassLoader" and the "loaderOfCheckedCode".
 */
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
