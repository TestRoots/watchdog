package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;

/**
 * Base class for managing intervals. Provides basic functionality for getting
 * certain types of intervals.
 */
public class IntervalManagerBase {

	/** A list of the managed intervals. */
	protected List<IntervalBase> intervals = new ArrayList<IntervalBase>();

	/**
	 * @return Returns a list of intervals of the given class, if there is any
	 *         such open. If not, returns the empty list.
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntervalBase> List<T> getIntervals(Class<T> clazz) {
		List<T> collectedIntervals = new ArrayList<T>();
		for (IntervalBase interval : intervals) {
			// if (clazz.isAssignableFrom(interval.getClass())) {
			if (clazz.isInstance(interval)) {
				collectedIntervals.add((T) interval);
			}
		}

		return collectedIntervals;
	}

	/**
	 * @return Returns an interval of the given class, if there is any such
	 *         open. If not, returns null.
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntervalBase> T getInterval(Class<T> clazz) {
		for (IntervalBase interval : intervals) {
			// if (clazz.isAssignableFrom(interval.getClass())) {
			if (clazz.isInstance(interval)) {
				return (T) interval;
			}
		}

		return null;
	}

	/**
	 * @return An {@link ArrayList} of intervals of the specified document type.
	 */
	protected List<IntervalBase> getEditorIntervalsOfDocType(DocumentType type) {
		List<IntervalBase> collectedIntervals = new ArrayList<IntervalBase>();
		for (IntervalBase interval : intervals) {
			if (interval instanceof EditorIntervalBase) {
				EditorIntervalBase editorInterval = (EditorIntervalBase) interval;
				if (editorInterval.getDocument().getDocumentType() == type) {
					collectedIntervals.add(interval);
				}
			}
		}
		return collectedIntervals;
	}
}
