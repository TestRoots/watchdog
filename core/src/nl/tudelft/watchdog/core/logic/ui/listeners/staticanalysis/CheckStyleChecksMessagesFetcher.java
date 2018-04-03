package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.core.util.WatchDogUtilsBase;

/**
 * Warning: This class is full of classloader fixes, reflection, hacks to support Checkstyle warnings in IntelliJ
 * and Eclipse. There are Checkstyle IDE plugins (for Eclipse and IntelliJ) and the plain Checkstyle jar itself.
 * The main complication comes from IntelliJ depending on Checkstyle via dynamically loading its jar over
 * a {@link ClassLoader}. In Eclipse, we do not know whether the user has installed the Checkstyle plugin at all,
 * which means we can also only dynamically depend on the Checkstyle classes.
 *
 * We need multiple classloaders to load the correct files to load the CheckStyle class files and one
 * to load the CheckStyle message.properties files. The first classloader is created in the IntelliJ CheckStyle plugin.
 * This plugin is configured to load several jars, including the CheckStyle jar. This loader therefore has access to
 * CheckStyle classes. In the code below, this loader is called <code>checkStylePluginClassLoader</code>.
 *
 * Besides the actual classes, other configuration resources from Checkstyle must also be loaded. This configuration
 * however partly resides in the CheckStyle jar. Other files reside in the IntelliJ plugin. To that end, the IntelliJ
 * plugin implements a so-called "csaccess" layer. This layer has its own classloader and has access to:
 * 1. the csaccess files as implemented by the plugin and 2. the resources available in CheckStyle.
 * This loader is named <code>checkStylePackageLoaderClassLoader</code> in the code below.
 *
 * The overall implementation design is therefore as follows: Load the configuration and other plugin-relevant
 * classes in our current classloader. Once we got the relevant classes, obtain the classloader created by
 * the plugin and load the CheckStyle checks (as defined in "com.puppycrawl.tools.checkstyle.checks"). Once we got
 * all checks, we obtain their corresponding messages.
 *
 * Since these messages are stored in resources named "messages.properties" and based on the packagename,
 * we need to use the last classloader to fetch the resources from there. We process the resources in
 * {@link #addMessagesToCheckStyleBundleForLoadedChecks(ClassLoader, Map, ClassLoader)}.
 * Observe that this method takes 2 classloaders, as we need both the classes (to obtain the resource path),
 * e.g. the <code>checkStylePluginClassLoader</code> and the other one to obtain the cactual resource, e.g.
 * <code>checkStylePackageLoaderClassLoader</code>.
 *
 * If you need to update this code, think carefully about which classloader can load what.
 * When we originally wrote this class, it was mostly based on
 * {@see https://github.com/jshiell/checkstyle-idea/blob/master/src/csaccess/java/org/infernus/idea/checkstyle/service/cmd/OpCreateChecker.java}
 * This class contained important documentation about its usage of "moduleClassLoader" and the "loaderOfCheckedCode".
 */
public class CheckStyleChecksMessagesFetcher {

    public static void addCheckStyleMessagesToBundle(ClassLoader classLoader)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, IOException {
        addCheckStyleMessagesToBundle(classLoader, classLoader);
    }

    public static void addCheckStyleMessagesToBundle(ClassLoader checkStylePackageLoaderClassLoader,
            ClassLoader checkStylePluginClassLoader)
                    throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, IOException {
        final Map<String, String> nameToModuleName = getModuleMapFromPackageObjectFactory(checkStylePackageLoaderClassLoader);

        addMessagesToCheckStyleBundleForLoadedChecks(checkStylePackageLoaderClassLoader, nameToModuleName, checkStylePluginClassLoader);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getModuleMapFromPackageObjectFactory(ClassLoader checkStylePackageLoaderClassLoader)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {

        final Class<?> packageObjectFactoryClass = checkStylePackageLoaderClassLoader
                .loadClass("com.puppycrawl.tools.checkstyle.PackageObjectFactory");

        final Field packageObjectFactoryMapField = packageObjectFactoryClass.getDeclaredField("NAME_TO_FULL_MODULE_NAME");
        packageObjectFactoryMapField.setAccessible(true);

        return (Map<String, String>) packageObjectFactoryMapField.get(null);
    }

    private static void addMessagesToCheckStyleBundleForLoadedChecks(
            ClassLoader checkStylePackageLoaderClassLoader,
            Map<String, String> nameToModuleName,
            ClassLoader checkStylePluginClassLoader) throws IOException {
        // Use a map to have distinct resource properties, to make sure we don't process messages double
        Map<String, String> resourceToPackage = new HashMap<>();

        nameToModuleName.values().stream()
                .map(WatchDogUtilsBase.transformCheckedExceptionIntoUncheckedException(checkStylePackageLoaderClassLoader::loadClass))
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

    public static void addMessageToCheckstyleBundle(String name, String message) {
        try {
            StaticAnalysisMessageClassifier.CHECKSTYLE_BUNDLE.addMessage(
                    "checkstyle." + name,
                    message
            );
        } catch (Exception ignored) {
            WatchDogLogger.getInstance().logSevere("Could not create CheckStyle pattern for key \"" + name + "\"");
        }
    }

}
