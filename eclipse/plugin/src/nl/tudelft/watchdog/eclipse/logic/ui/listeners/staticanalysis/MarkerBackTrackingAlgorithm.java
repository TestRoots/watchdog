package nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CoreMarkupModelListener;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.Warning;

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
class MarkerBackTrackingAlgorithm {

	private final List<MarkerHolder> oldMarkers;
    private final List<MarkerHolder> currentMarkers;
    private final int[][] memoization;
    private final int oldSize;
    private final int currentSize;
    List<Warning<String>> createdWarningTypes;
    List<Warning<String>> removedWarningTypes;

    MarkerBackTrackingAlgorithm(List<MarkerHolder> oldMarkers, List<MarkerHolder> currentMarkers) {
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
