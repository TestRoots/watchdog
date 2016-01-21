package nl.tudelft.watchdog.core.logic.interval;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.TransferManagerBase;
import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;

/**
 * This manager takes care of the repeated transferal of all closed intervals to
 * the server. When the transfer to the server was successful, the intervals are
 * immediately deleted from the local database. Furthermore, it allows the
 * immediate execution of this regularly scheduled task, e.g. when it is needed
 * on exiting.
 */
public class IntervalTransferManagerBase extends TransferManagerBase<IntervalBase> {

	private static final int UPDATE_RATE = 3 * 60 * 1000;

	/**
	 * Constructor. Tries to immediately transfer all remaining intervals, and
	 * sets up a scheduled timer to run every {@value #UPDATE_RATE}
	 * milliseconds.
	 */
	public IntervalTransferManagerBase(final IntervalPersisterBase intervalPersisterBase, String projectName) {
		super(intervalPersisterBase, projectName, UPDATE_RATE);
	}

	@Override
	protected JsonTransferer<IntervalBase> createTransferer() {
		return new IntervalJsonTransferer();
	}

	@Override
	protected void updateStatisticsPreferences(int transferredIntervals) {
		PreferencesBase prefs = WatchDogGlobals.getPreferences();
		prefs.setLastTransferedInterval();
		prefs.addTransferedIntervals(transferredIntervals);		
	}
}
