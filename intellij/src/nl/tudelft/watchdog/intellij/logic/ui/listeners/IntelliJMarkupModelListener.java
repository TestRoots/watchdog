package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.event.MarkupModelListener;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import org.jetbrains.annotations.NotNull;

public class IntelliJMarkupModelListener implements MarkupModelListener {
    private final TrackingEventManager trackingEventManager;

    IntelliJMarkupModelListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
    }

    @Override
    public void afterAdded(@NotNull RangeHighlighterEx rangeHighlighterEx) {
    }

    @Override
    public void beforeRemoved(@NotNull RangeHighlighterEx rangeHighlighterEx) {
    }

    @Override
    public void attributesChanged(@NotNull RangeHighlighterEx rangeHighlighterEx, boolean b, boolean b1) {
    }
}
