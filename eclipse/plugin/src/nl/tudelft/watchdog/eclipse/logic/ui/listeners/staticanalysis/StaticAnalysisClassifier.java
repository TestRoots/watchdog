package nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis;

import java.util.Locale;

import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;

import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.ClassificationBundle;

@SuppressWarnings("restriction")
public class StaticAnalysisClassifier {

    private static final ClassificationBundle ECLIPSE_BUNDLE = new ClassificationBundle();

    static {
        HashtableOfInt hashTable = DefaultProblemFactory.loadMessageTemplates(Locale.getDefault());

        for (int key : hashTable.keyTable) {
            Object value = hashTable.get(key);
            if (value != null && value instanceof String) {
                ECLIPSE_BUNDLE.addMessage(String.valueOf(key), (String) value);
            }
        }

        ECLIPSE_BUNDLE.sortList();
    }

    public static String classify(String message) {
        return ECLIPSE_BUNDLE.getFromBundle(message);
    }

}
