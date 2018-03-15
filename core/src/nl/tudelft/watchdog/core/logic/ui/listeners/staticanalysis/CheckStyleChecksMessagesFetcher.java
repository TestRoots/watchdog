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
 * Moreover, in Eclipse we are not certain whether the plugin is actually on the classpath,
 * which means we have to dynamically load the classes as well.
 *
 * Now the real magic starts when you realize that there are multiple classloaders necessary to load
 * the correct files. The first classloader is created in the CheckStyle plugin. This plugin is configured
 * to load several jars, including the CheckStyle jar. This loader therefore has access to CheckStyle
 * classes. In the code below, this loader is called "checkStylePluginClassLoader".
 *
 * Besides the actual classes, other configuration must also be loaded. This configuration however partly
 * resides in CheckStyle and other files reside in the plugin. To that end, the IntelliJ plugin implements a so-called
 * "csaccess" layer, which lives besides CheckStyle. This layer has its own classloader and has access to:
 * 1. the csaccess files as implemented by the plugin and 2. the resources available in CheckStyle.
 * This loader is named "checkStylePackageLoaderClassLoader" in the code below.
 *
 *
 *
 * The overall logic of implementation is therefore as follows: Load the configuration and other plugin-relevant\
 * classes in our current classloader. Once we got the relevant classes, obtain the classloader created by
 * the plugin and load the
 * CheckStyle checks (as defined in "com.puppycrawl.tools.checkstyle.checks"). Once we got all checks,
 * we have to obtain their corresponding messages.
 *
 * Since these messages are stored in resources named "messages.properties" and based on the packagename,
 * we need to use the last classloader to fetch the resources from there. We process the resources in
 * {@link #addMessagesToCheckStyleBundleForLoadedChecks(ClassificationBundle, ClassLoader, Map, ClassLoader)}.
 * Observe that this method takes 2 classloaders, as we need both the classes (to obtain the resource path)
 * and the other one to obtain the actual resource.
 *
 *
 *
 * All in all, this journey took a long time to figure out. If you need to update this code, think carefully
 * about which classloader can load what. When I originally wrote this class, it was mostly based on
 * {@see https://github.com/jshiell/checkstyle-idea/blob/master/src/csaccess/java/org/infernus/idea/checkstyle/service/cmd/OpCreateChecker.java}
 * This class contained crucial documentation regarding its "moduleClassLoader" and the "loaderOfCheckedCode".
 */
public class CheckStyleChecksMessagesFetcher {

    public static void addCheckStyleMessagesToBundle(ClassificationBundle bundle,
            ClassLoader checkStylePackageLoaderClassLoader,
            ClassLoader checkStylePluginClassLoader)
                    throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, IOException {
        final Map<String, String> nameToModuleName = getModuleMapFromPackageObjectFactory(checkStylePackageLoaderClassLoader);

        addMessagesToCheckStyleBundleForLoadedChecks(bundle, checkStylePackageLoaderClassLoader, nameToModuleName, checkStylePluginClassLoader);
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
            ClassificationBundle bundle,
            ClassLoader checkStylePackageLoaderClassLoader,
            Map<String, String> nameToModuleName,
            ClassLoader checkStylePluginClassLoader) throws IOException {
        // Use a map to have distinct resource properties, to make sure we don't process messages double
        Map<String, String> resourceToPackage = new HashMap<>();

        nameToModuleName.values().stream()
                .map(WatchDogUtilsBase.unchecked(checkStylePackageLoaderClassLoader::loadClass))
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
                addMessageToCheckstyleBundle(bundle, entry.getValue() + "." + name, properties.getProperty(name));
            }
        }
    }

    public static void addMessageToCheckstyleBundle(ClassificationBundle bundle, String name, String message) {
        try {
            bundle.addMessage(
                    "checkstyle." + name,
                    message
            );
        } catch (Exception ignored) {
            WatchDogLogger.getInstance().logSevere("Could not create CheckStyle pattern for key \"" + name + "\"");
        }
    }

}
