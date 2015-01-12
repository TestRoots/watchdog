package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;

/**
 * Base class for managing intervals. Provides basic functionality for getting
 * certain types of intervals.
 */
public class IntervalManagerBase {

	/** A list of the managed intervals. */
	protected List<IntervalBase> intervals = new ArrayList<IntervalBase>();

	/**
	 * @return An interval of the specified type, or <code>null</code> if no
	 *         such interval is currently open.
	 */
	public IntervalBase getIntervalOfType(IntervalType type) {
		// TODO (MMB) leaves out is !closed check currently
		try {
			return getIntervalsOfType(type).get(0);
		} catch (RuntimeException exceptioin) {
			return null;
		}
	}

	/**
	 * @return An {@link ArrayList} of intervals of the specified type.
	 */
	protected List<IntervalBase> getIntervalsOfType(IntervalType type) {
		List<IntervalBase> collectedIntervals = new ArrayList<IntervalBase>();
		for (IntervalBase interval : intervals) {
			if (interval.getType() == type) {
				collectedIntervals.add(interval);
			}
		}
		return collectedIntervals;
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
