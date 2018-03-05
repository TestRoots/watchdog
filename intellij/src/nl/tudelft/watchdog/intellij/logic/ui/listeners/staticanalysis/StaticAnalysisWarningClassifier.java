package nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis;

import nl.tudelft.watchdog.core.util.WatchDogLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class StaticAnalysisWarningClassifier {

    // TODO(timvdlippe): Split up for full static strings and those that have variable patterns in it.
    // E.g. anything with '{\d+}' is a pattern, everything else just a static string.
    private static Map<Pattern, String> reverseKeySet = new HashMap<>();

    static {
        createPatternsForKeysInBundle(ResourceBundle.getBundle("messages.InspectionsBundle"));
        createPatternsForKeysInBundle(ResourceBundle.getBundle("com.siyeh.InspectionGadgetsBundle"));
    }

    private static void createPatternsForKeysInBundle(ResourceBundle bundle) {
        bundle.keySet().forEach(key -> {
            try {
                final String textualDescription = bundle.getString(key)
                        .replaceAll("''", "'")
                        .replaceAll("\\(", "\\(")
                        .replaceAll("\\)", "\\)")
                        .replaceAll("<code>#ref</code>", "'[^']+'")
                        .replaceAll("\\{.+}", ".+")
                        .replaceAll(" #loc", "")
                        .replaceAll(" #ref", "");

                if (!".+".equals(textualDescription)) {
                    reverseKeySet.put(Pattern.compile(textualDescription), key);
                }
            } catch (Exception ignored) {
                WatchDogLogger.getInstance().logSevere("Could not create pattern for key \"" + key + "\"");
            }
        });
    }

    public static String classify(String message) {
        return reverseKeySet.entrySet().stream()
                .filter(entry -> entry.getKey().matcher(message).matches())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("UNKNOWN");
    }
}
