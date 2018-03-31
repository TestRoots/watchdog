package nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.joda.time.DateTime;

import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier;

/**
 * Because an {@link IMarker} throws exceptions on {@link IMarker#getAttribute(String)} if it is removed,
 * we have to extract all useful information at the first moment we encounter it, instead of invoking
 * these methods once the marker is removed. At this moment, we are only interested in its message,
 * as all other attributes are non-unique (yes even the ID is not unique). The only way to keep track
 * of messages in a somewhat unique manner is its message, which does include private information.
 * Make sure that when you use this message, the data is anonymized.
 */
class MarkerHolder implements Comparable<MarkerHolder> {
    String message;
    int lineNumber;
    DateTime warningCreationTime;

    static MarkerHolder fromIMarker(IMarker marker) {
        MarkerHolder holder = new MarkerHolder();
        holder.message = marker.getAttribute(IMarker.MESSAGE, "");
        holder.lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, 0);
        holder.warningCreationTime = DateTime.now();

        try {
            if (EclipseMarkupModelListener.CHECKSTYLE_MARKER_ID.equals(marker.getType())) {
                holder.message = StaticAnalysisMessageClassifier.START_OF_CHECKSTYLE_MESSAGE + holder.message;
            }
        } catch (CoreException ignored) {
            // Not supposed to happen
        }

        return holder;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MarkerHolder) {
            return this.message.equals(((MarkerHolder) other).message);
        }
        return false;
    }

    @Override
    public int compareTo(MarkerHolder other) {
        return Integer.compare(this.lineNumber, other.lineNumber);
    }
}