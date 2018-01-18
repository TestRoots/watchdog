package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.event.MarkupModelListener;
import org.jetbrains.annotations.NotNull;

class IntelliJMarkupModelListener extends MarkupModelListener.Adapter {
    @Override
    public void beforeRemoved(@NotNull RangeHighlighterEx rangeHighlighterEx) {
    }

    @Override
    public void afterAdded(@NotNull RangeHighlighterEx rangeHighlighterEx) {
    }
}
