package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

/**
 * Holder for all {@link ClassificationBundle}s that can contain messages.
 * For each supported Plugin, one {@link ClassificationBundle} exists, with one bundle
 * for the messages in the default IDE. These messages are obtained and processed
 * in the respective subclass of {@link CoreMarkupModelListener}.
 */
public class StaticAnalysisMessageClassifier {

    /**
     * The start of a CheckStyle static analysis warning.
     */
    public static final String START_OF_CHECKSTYLE_MESSAGE = "Checkstyle: ";

    public static final ClassificationBundle IDE_BUNDLE = new ClassificationBundle();
    public static final ClassificationBundle CHECKSTYLE_BUNDLE = new ClassificationBundle();

    /**
     * Classify the passed in message to its corresponding key. The keys are fetched
     * from the corresponding {@link ClassificationBundle}. For more information and examples,
     * see {@link ClassificationBundle#getFromBundle(String)}.
     *
     * @param message A concrete static analysis message.
     * @return The key of the message pattern that was used to create the message.
     */
    public static String classify(String message) {
        if (message.startsWith(START_OF_CHECKSTYLE_MESSAGE)) {
            return CHECKSTYLE_BUNDLE.getFromBundle(message.substring(START_OF_CHECKSTYLE_MESSAGE.length()));
        }
        return IDE_BUNDLE.getFromBundle(message);
    }

}
