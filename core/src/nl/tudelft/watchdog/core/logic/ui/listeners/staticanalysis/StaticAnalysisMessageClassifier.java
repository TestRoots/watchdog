package nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis;

/**
 * Holder for all {@link ClassificationBundle} that can contain messages.
 * For each supported Plugin, 1 {@link ClassificationBundle} exists, with one bundle
 * for the messages in the default IDE. These messages are obtained and processed
 * in the respective subclass of {@link CoreMarkupModelListener}.
 */
public class StaticAnalysisMessageClassifier {

    public static final String START_OF_CHECKSTYLE_MESSAGE = "Checkstyle: ";

    public static final ClassificationBundle IDE_BUNDLE = new ClassificationBundle();
    public static final ClassificationBundle CHECKSTYLE_BUNDLE = new ClassificationBundle();

    public static String classify(String message) {
        if (message.startsWith(START_OF_CHECKSTYLE_MESSAGE)) {
            return CHECKSTYLE_BUNDLE.getFromBundle(message.substring(START_OF_CHECKSTYLE_MESSAGE.length()));
        }
        return IDE_BUNDLE.getFromBundle(message);
    }

}
