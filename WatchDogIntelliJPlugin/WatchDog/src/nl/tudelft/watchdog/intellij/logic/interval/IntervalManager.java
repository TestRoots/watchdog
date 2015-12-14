package nl.tudelft.watchdog.intellij.logic.interval;

import nl.tudelft.watchdog.core.logic.interval.IDEIntervalManagerBase;
import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.intellij.logic.document.DocumentCreator;
import nl.tudelft.watchdog.intellij.logic.document.EditorWrapper;

/**
 * The interval manager for the IntelliJ plugin
 */
public class IntervalManager extends IDEIntervalManagerBase {

    /**
     * Constructor.
     */
    public IntervalManager(IntervalPersisterBase intervalsToTransferPersister,
                           IntervalPersisterBase intervalsStatisticsPersister) {
        super(intervalsToTransferPersister, intervalsStatisticsPersister);
    }

    @Override
    protected void setEndingDocumentOf(TypingInterval typingInterval) {
        typingInterval.setEndingDocument(DocumentCreator
                .createDocument(((EditorWrapper) typingInterval.getEditorWrapper()).getEditor()));
    }
}
