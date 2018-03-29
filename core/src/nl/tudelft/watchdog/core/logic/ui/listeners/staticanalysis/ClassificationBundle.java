package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import nl.tudelft.watchdog.core.util.WatchDogLogger;

/**
 * Bundle that can retrieve keys of Static Analysis warning messages based on two data-structures:
 * A hash-map for O(1) lookup and a List of patterns for O(n) matching.
 *
 * Every message is first statically looked up in the HashMap. These will match for all messages that
 * are static and do not contain any dynamic parts.
 *
 * If there is no result, the message will be matched against all {@link PatternBasedKey} in {@link #patternBasedKeyList}.
 * This list is ordered from most-specific message (e.g. longest) to shortest.
 */
public class ClassificationBundle {

    /**
     * Use a pattern instead of {@link String#matches(String)}, because matches wants to match the whole
     * string, and we need to do a "contains" instead.
     */
    private static final Pattern BRACKETS_PATTERN = Pattern.compile("\\{.+}");

    private final Map<String, String> staticKeyMap = new HashMap<>();
    private final List<PatternBasedKey> patternBasedKeyList = new ArrayList<>();

    public void createPatternsForKeysInBundle(String bundleName) {
        final ResourceBundle bundle = ResourceBundle.getBundle(bundleName);

        bundle.keySet().forEach(key -> {
            try {
                this.addMessage(key, bundle.getString(key));
            } catch (Exception ignored) {
                WatchDogLogger.getInstance().logSevere("Could not create pattern for key \"" + key + "\"");
            }
        });
    }

    public void addMessage(String key, String message) {
        if (containsDynamicParts(message)) {
            final String regex = message
                    .replaceAll("'''", "'")
                    .replaceAll("''", "'")
                    .replaceAll("\\(", "\\\\(")
                    .replaceAll("\\)", "\\\\)")
                    .replaceAll("\\[", "\\\\[")
                    .replaceAll("<code>[^<]+</code>", "'[^']+'")
                    // Perform the next replace twice, as there can be nested brackets. For example:
                    // {1, choice, 1#direct or indirect implementation|2#{1,number} direct or indirect implementations}
                    .replaceAll("\\{[^{}]+}", ".+")
                    .replaceAll("\\{[^{}]+}", ".+")
                    .replaceAll("\\{}", "\\\\{}")
                    .replaceAll(" #loc", "")
                    .replaceAll(" #ref", "");

            // Filter out the "catch-all" patterns. These would almost always be erroneous matches.
            // Note that we are matching a "regex" (it is a String) with a regex String
            // Therefore escape the dot and the plus, as these are the actual patterns we have
            if (!regex.matches("(\\.\\+)+")) {
                patternBasedKeyList.add(new PatternBasedKey(Pattern.compile(regex), key));
            }
        } else {
            staticKeyMap.put(message, key);
        }
    }

    String getFromBundle(String message) {
        // For performance reasons, we first do a static lookup. In this case, the message
        // does not contain any dynamic parts. This lookup is O(1) and catches most of the cases.
        // If instead the message contains dynamic parts, we have to pattern match in the other list.
        // This list is sorted on longest patterns first, to make sure we are most-specific first.
        String key = staticKeyMap.get(message);

        if (key == null) {
            // Use the Java Stream API with "findFirst", to make sure we bail out as soon as possible,
            // to not do any unnecessary work.
            key = patternBasedKeyList.stream()
                    .filter(keyPattern -> keyPattern.pattern.matcher(message).matches())
                    .map(keyPattern -> keyPattern.key)
                    .findFirst()
                    .orElse("unknown");
        }

        return key;
    }

    /**
     * Sort the list on longest pattern first.
     */
    public void sortList() {
        Collections.sort(patternBasedKeyList);
    }

    private static boolean containsDynamicParts(String message) {
        return message.contains("#loc")
                || message.contains("#ref")
                || BRACKETS_PATTERN.matcher(message).find();
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
        public int compareTo(PatternBasedKey other) {
            // Reverse the order of this and other here, because we want
            // the list to be sorted from largest pattern to shortest.
            return Integer.compare(other.length, this.length);
        }
    }
}
