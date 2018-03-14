package nl.tudelft.watchdog.intellij.logic.ui.listeners.staticanalysis;

import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.ClassificationBundle;

class StaticAnalysisWarningClassifier {

    private static final String START_OF_CHECKSTYLE_MESSAGE = "Checkstyle: ";

    private static final ClassificationBundle INTELLIJ_BUNDLE = new ClassificationBundle();
    static final ClassificationBundle CHECKSTYLE_BUNDLE = new ClassificationBundle();

    static {
        INTELLIJ_BUNDLE.createPatternsForKeysInBundle("messages.InspectionsBundle");
        INTELLIJ_BUNDLE.createPatternsForKeysInBundle("com.siyeh.InspectionGadgetsBundle");

        INTELLIJ_BUNDLE.sortList();
    }

    static String classify(String message) {
        if (message.startsWith(START_OF_CHECKSTYLE_MESSAGE)) {
            return CHECKSTYLE_BUNDLE.getFromBundle(message.substring(START_OF_CHECKSTYLE_MESSAGE.length()));
        }
        return INTELLIJ_BUNDLE.getFromBundle(message);
    }
}
