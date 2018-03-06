package nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis;

import nl.tudelft.watchdog.core.util.WatchDogLogger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

class StaticAnalysisWarningClassifier {

    /**
     * Use a pattern instead of {@link String#matches(String)}, because matches wants to match the whole
     * string, and we need to do a "contains" instead.
     */
    private static final Pattern BRACKETS_PATTERN = Pattern.compile("\\{.+}");

    private static final Map<String, String> staticKeyMap = new HashMap<>();
    private static final List<PatternBasedKey> patternBasedKeyList = new ArrayList<>();

    static {
        createPatternsForKeysInBundle(ResourceBundle.getBundle("messages.InspectionsBundle"));
        createPatternsForKeysInBundle(ResourceBundle.getBundle("com.siyeh.InspectionGadgetsBundle"));

        // Sort the list on longest pattern first
        Collections.sort(patternBasedKeyList);
    }

    private static void createPatternsForKeysInBundle(ResourceBundle bundle) {
        bundle.keySet().forEach(key -> {
            try {
                final String message = bundle.getString(key);

                if (containsDynamicParts(message)) {
                    final String regex = message.replaceAll("''", "'")
                            .replaceAll("\\(", "\\\\(")
                            .replaceAll("\\)", "\\\\)")
                            .replaceAll("\\[", "\\\\[")
                            .replaceAll("<code>[^<]+</code>", "'[^']+'")
                            // Perform the next replace twice, as there can be nested brackets. For example:
                            // {1, choice, 1#direct or indirect implementation|2#{1,number} direct or indirect implementations}
                            .replaceAll("\\{[^{}]+}", ".+")
                            .replaceAll("\\{[^{}]+}", ".+")
                            .replaceAll(" #loc", "")
                            .replaceAll(" #ref", "");

                    // Filter out the "catch-all" patterns. These would almost always be erroneous matches.
                    if (!".+".equals(regex)) {
                        patternBasedKeyList.add(new PatternBasedKey(Pattern.compile(regex), key));
                    }
                } else {
                    staticKeyMap.put(message, key);
                }
            } catch (Exception ignored) {
                WatchDogLogger.getInstance().logSevere("Could not create pattern for key \"" + key + "\"");
            }
        });
    }

    private static boolean containsDynamicParts(String message) {
        return message.contains("#loc")
                || message.contains("#ref")
                || BRACKETS_PATTERN.matcher(message).find();
    }

    static String classify(String message) {
        // For performance reasons, we first do a static lookup. In this case, the message
        // does not contain any dynamic parts. This lookup is O(1) and catches most of the cases.
        // If instead the message contains dynamic parts, we have to pattern match in the other list.
        // This list is sorted on longest patterns first, to make sure we are most-specific first.
        //
        // Use the Java Stream API with "findFirst", to make sure we bail out as soon as possible,
        // to not do any unnecessary work.
        return staticKeyMap.getOrDefault(message,
                patternBasedKeyList.stream()
                        .filter(key -> key.pattern.matcher(message).matches())
                        .map(key -> key.key)
                        .findFirst()
                        .orElse("unknown")
        );
    }

    private static final class PatternBasedKey implements Comparable<PatternBasedKey> {
        private final Pattern pattern;
        private final String key;
        private final int length;

        PatternBasedKey(Pattern pattern, String key) {
            this.pattern = pattern;
            this.key = key;
            this.length = pattern.pattern().length();
        }

        @Override
        public int compareTo(@NotNull PatternBasedKey other) {
            // Reverse the order of this and other here, because we want
            // the list to be sorted from largest pattern to shortest.
            return Integer.compare(other.length, this.length);
        }
    }
}
