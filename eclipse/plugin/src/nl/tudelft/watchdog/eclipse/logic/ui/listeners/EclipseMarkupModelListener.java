package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CheckStyleChecksMessagesFetcher;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CoreMarkupModelListener;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.FileWarningSnapshotEvent;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.Warning;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.eclipse.logic.document.DocumentCreator;

@SuppressWarnings("restriction")
public class EclipseMarkupModelListener extends CoreMarkupModelListener implements IResourceChangeListener {

    static {
        HashtableOfInt hashTable = DefaultProblemFactory.loadMessageTemplates(Locale.getDefault());

        for (int key : hashTable.keyTable) {
            Object value = hashTable.get(key);
            if (value != null && value instanceof String) {
                StaticAnalysisMessageClassifier.IDE_BUNDLE.addMessage(String.valueOf(key), (String) value);
            }
        }

        StaticAnalysisMessageClassifier.IDE_BUNDLE.sortList();

        try {
            ClassLoader currentClassLoader = EclipseMarkupModelListener.class.getClassLoader();
            CheckStyleChecksMessagesFetcher.addCheckStyleMessagesToBundle(StaticAnalysisMessageClassifier.CHECKSTYLE_BUNDLE, currentClassLoader, currentClassLoader);

            StaticAnalysisMessageClassifier.CHECKSTYLE_BUNDLE.sortList();
        } catch (Exception ignored) {
            // CheckStyle apparently was not loaded, bail out
            ignored.printStackTrace();
        }
    }

    private Map<IPath, List<MarkerHolder>> oldFileMarkers;
    private Map<IPath, List<MarkerHolder>> currentFileMarkers;

    public EclipseMarkupModelListener(TrackingEventManager trackingEventManager) {
        super(trackingEventManager);
        this.oldFileMarkers = new HashMap<>();
        this.currentFileMarkers = new HashMap<>();
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            this.oldFileMarkers = this.currentFileMarkers;
            this.currentFileMarkers = new HashMap<>();
            event.getDelta().accept(this.createVisitor(false));
        } catch (CoreException e) {
            WatchDogLogger.getInstance().logSevere(e.getMessage());
        }
    }

    ResourceAndResourceDeltaVisitor createVisitor(boolean shouldCreateSnapshot) {
        return new ResourceAndResourceDeltaVisitor(shouldCreateSnapshot);
    }

    private class ResourceAndResourceDeltaVisitor implements IResourceDeltaVisitor, IResourceVisitor {

        private boolean shouldCreateSnapshot;

        public ResourceAndResourceDeltaVisitor(boolean shouldCreateSnapshot) {
            this.shouldCreateSnapshot = shouldCreateSnapshot;
        }

        /**
         * Can be new resources, deleted or simply changed. In any case, even if we do not have
         * previous state in {@link EclipseMarkupModelListener#oldFileMarkers} we need to compute
         * a diff.
         */
        @Override
        public boolean visit(IResourceDelta delta) throws CoreException {
            return this.visit(delta.getResource(), true);
        }

        /**
         * Invoked on initial project open to setup our initial state for {@link EclipseMarkupModelListener#oldFileMarkers}.
         */
        @Override
        public boolean visit(IResource resource) throws CoreException {
            return this.visit(resource, false);
        }

        /**
         * The Eclipse API trashes and recreates all markers after a build.
         * This means that we have to handle two lists of markers and compute
         * the difference between them. Delegate this implementation to
         * {@link MarkerBackTrackingAlgorithm} which is an abstraction around
         * this algorithm.
         */
        private boolean visit(IResource resource, boolean shouldComputeDiff) throws CoreException {
            if (!resource.exists()) {
                return false;
            }

            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                IPath filePath = file.getFullPath();
                List<MarkerHolder> oldMarkers = oldFileMarkers.get(filePath);
                List<MarkerHolder> currentMarkers =
                        Arrays.stream(file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO))
                              .map(MarkerHolder::fromIMarker)
                              .sorted()
                              .collect(Collectors.toList());

                if (shouldComputeDiff && oldMarkers == null) {
                    oldMarkers = Collections.emptyList();
                }
                if (this.shouldCreateSnapshot) {
                    createWarningSnapshotForMarkers(currentMarkers);
                }

                if (oldMarkers != null) {
                    Document document = DocumentCreator.createDocument(file.getName(), file).prepareDocument();

                    new MarkerBackTrackingAlgorithm(document, oldMarkers, currentMarkers)
                            .computeMemoizationTable()
                            .traverseMemoizationTable();
                }

                currentFileMarkers.put(filePath, currentMarkers);
            }

            return true;
        }
    }

    private void createWarningSnapshotForMarkers(List<MarkerHolder> currentMarkers) {
        List<Warning<String>> warnings = currentMarkers.stream()
                .map(holder -> new Warning<>(
                        StaticAnalysisMessageClassifier.classify(holder.message),
                        holder.lineNumber,
                        DateTime.now().toDate()
                    ))
                .collect(Collectors.toList());

        this.trackingEventManager.addEvent(new FileWarningSnapshotEvent(document, warnings));
    }

    /**
     * Because an {@link IMarker} throws exceptions on {@link IMarker#getAttribute(String)} if it is removed,
     * we have to extract all useful information at the first moment we encounter it, instead of invoking
     * these methods once the marker is removed. At this moment, we are only interested in its message,
     * as all other attributes are non-unique (yes even the ID is not unique). The only way to keep track
     * of messages in a somewhat unique manner is its message, which does include private information.
     * Make sure that when you use this message, the data is anonymized.
     */
    static class MarkerHolder implements Comparable<MarkerHolder> {
        static final String CHECKSTYLE_MARKER_ID = "net.sf.eclipsecs.core.CheckstyleMarker";

        private String message;
        private int lineNumber;
        private DateTime warningCreationTime;

        static MarkerHolder fromIMarker(IMarker marker) {
            MarkerHolder holder = new MarkerHolder();
            holder.message = marker.getAttribute(IMarker.MESSAGE, "");
            holder.lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, 0);
            holder.warningCreationTime = DateTime.now();

            try {
                if (CHECKSTYLE_MARKER_ID.equals(marker.getType())) {
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

    /**
     * An implementation of an algorithm to solve the longest common subsequence problem.
     * The implementation relies on a memoization table to compute (in O(n * m)) all lengths
     * of cost of modifications to the lists. It then traverses through this matrix
     * and based on the cost either triggers a deletion or an addition.
     *
     * There are multiple viable implementations of this diffing
     * algorithm and this abstraction allows us to later change the actual
     * implementation without breaking the usage contract.
     *
     * @see https://en.wikipedia.org/wiki/Longest_common_subsequence_problem
     */
    private class MarkerBackTrackingAlgorithm {

        private final Document document;
        private final List<MarkerHolder> oldMarkers;
        private final List<MarkerHolder> currentMarkers;
        private final int[][] memoization;
        private final int oldSize;
        private final int currentSize;
        private List<Warning<String>> createdWarningTypes;
        private List<Warning<String>> removedWarningTypes;

        MarkerBackTrackingAlgorithm(Document document, List<MarkerHolder> oldMarkers, List<MarkerHolder> currentMarkers) {
            this.document = document;
            this.oldMarkers = oldMarkers;
            this.currentMarkers = currentMarkers;
            this.oldSize = oldMarkers.size();
            this.currentSize = currentMarkers.size();
            this.memoization = new int[oldSize + 1][currentSize + 1];
            this.createdWarningTypes = new ArrayList<>();
            this.removedWarningTypes = new ArrayList<>();
        }

        MarkerBackTrackingAlgorithm computeMemoizationTable() {
            for (int row = 0; row < oldSize; row++) {
                for (int column = 0; column < currentSize; column++) {
                    if (oldMarkers.get(row).equals(currentMarkers.get(column))) {
                        memoization[row + 1][column + 1] = memoization[row][column] + 1;
                    } else {
                        memoization[row + 1][column + 1] = Math.max(memoization[row + 1][column], memoization[row][column + 1]);
                    }
                }
            }

            return this;
        }

        void traverseMemoizationTable() {
            traverseMemoizationTable(oldSize, currentSize);

            addCreatedWarnings(this.createdWarningTypes.stream().map(this::createWarning), this.document);
            addRemovedWarnings(this.removedWarningTypes.stream().map(this::createWarning), this.document);
        }

        private Warning<String> createWarning(Warning<String> warning) {
            return new Warning<>(
                    StaticAnalysisMessageClassifier.classify(warning.type),
                    warning.lineNumber,
                    warning.warningCreationTime,
                    warning.secondsBetween
            );
        }

        /**
         * Traverse the memoized matrix and based on the length of the path
         * either invoke addCreatedWarning or addRemovedWarning as defined in
         * {@link CoreMarkupModelListener}.
         *
         * @param row The current row in the matrix
         * @param column The current column in the matrix
         */
        void traverseMemoizationTable(int row, int column) {
            // The markers at this position are equal, just continue traversing through the matrix
            if (row > 0 && column > 0 && oldMarkers.get(row - 1).equals(currentMarkers.get(column - 1))) {
                traverseMemoizationTable(row - 1, column - 1);
            // The markers are not the same. In this case, the length to traverse by choosing the column
            // is longer, which means that a new marker was added (as the column is larger) in currentMarkers
            } else if (column > 0 && (row == 0 || memoization[row][column - 1] >= memoization[row - 1][column])) {
                MarkerHolder warning = currentMarkers.get(column - 1);

                this.createdWarningTypes.add(new Warning<>(warning.message, warning.lineNumber, DateTime.now().toDate()));

                traverseMemoizationTable(row, column - 1);
            // In this case, the row length is lower, which means that the oldMarkers was longer at this point
            // Therefore it is a removed warning
            } else if (row > 0 && (column == 0 || memoization[row][column - 1] < memoization[row - 1][column])) {
                MarkerHolder warning = oldMarkers.get(row - 1);
                DateTime now = DateTime.now();

                this.removedWarningTypes.add(new Warning<>(warning.message, warning.lineNumber, now.toDate(), Seconds.secondsBetween(warning.warningCreationTime, now).getSeconds()));

                traverseMemoizationTable(row - 1, column);
            }
            // In this case we are at the end of the matrix (0th row/column), so nothing  happens
        }
    }
}
