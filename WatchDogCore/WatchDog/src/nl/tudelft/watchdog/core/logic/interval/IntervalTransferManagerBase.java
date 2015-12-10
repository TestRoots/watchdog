package nl.tudelft.watchdog.core.logic.interval;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.logic.ui.RegularCheckerBase;
import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

/**
 * This manager takes care of the repeated transferal of all closed intervals to
 * the server. When the transfer to the server was successful, the intervals are
 * immediately deleted from the local database. Furthermore, it allows the
 * immediate execution of this regularly scheduled task, e.g. when it is needed
 * on exiting.
 */
public class IntervalTransferManagerBase extends RegularCheckerBase {

	private static final int UPDATE_RATE = 3 * 60 * 1000;

	/**
	 * Constructor. Tries to immediately transfer all remaining intervals, and
	 * sets up a scheduled timer to run every {@value #UPDATE_RATE}
	 * milliseconds.
	 */
	public IntervalTransferManagerBase(final IntervalPersisterBase intervalPersisterBase, String projectName) {
		super(UPDATE_RATE);
		task = new IntervalsTransferTimerTask(intervalPersisterBase, projectName);
		runSetupAndStartTimeChecker();
	}

	/** 
	 * Immediately synchronizes the intervals with the server. 
	 * This function is only called before shutting down the IDE and it first 
	 * shortens the connection timeout to reduce the shutdown time in some cases.
	 */
	public void sendIntervalsImmediately() {
		NetworkUtils.setConnectionTimeout(3000);
		task.run();
	}

    protected static void refreshUI() {}

	private static class IntervalsTransferTimerTask extends TimerTask {
		private final IntervalPersisterBase intervalPersister;
		private final PreferencesBase preferences;
        private final String projectName;

		private IntervalsTransferTimerTask(IntervalPersisterBase intervalPersisterBase, String projectName) {
			this.intervalPersister = intervalPersisterBase;
			this.preferences = WatchDogGlobals.getPreferences();
            this.projectName = projectName;
		}

		/**
		 * Transfers all intervals from the persistence storage that are not yet
		 * on the server, to the server.
		 */
		@Override
		public void run() {
            if (intervalPersister.isClosed()) {
                return;
            }

			List<IntervalBase> intervalsToTransfer = new ArrayList<IntervalBase>(
					intervalPersister.readIntervals());

			if (intervalsToTransfer.isEmpty()) {
				return;
			}

			transferIntervals(intervalsToTransfer);
			resetDatabase();
			refreshUI();
		}

		private void transferIntervals(List<IntervalBase> intervalsToTransfer) {
			JsonTransferer intervalTransferer = new JsonTransferer();

			Connection connection = intervalTransferer.sendIntervals(
					intervalsToTransfer, projectName);
			switch (connection) {
			case SUCCESSFUL:
				intervalPersister.removeIntervals(intervalsToTransfer);
				preferences.setLastTransferedInterval();
				preferences.addTransferedIntervals(intervalsToTransfer.size());
				WatchDogGlobals.lastTransactionFailed = false;
				break;

			case NETWORK_ERROR:
				if (WatchDogGlobals.lastTransactionFailed) {
					// two transactions in a row failed. The user is likely
					// working without internet, so do not try to re-send
					// intervals
					return;
				}
				WatchDogGlobals.lastTransactionFailed = true;
				break;

			case UNSUCCESSFUL:
				WatchDogGlobals.lastTransactionFailed = true;
				int intervals = intervalsToTransfer.size();

				if (intervals == 1) {
					WatchDogLogger
							.getInstance()
							.logSevere(
									"Could not transfer interval and removed permanently!");
					intervalPersister.removeIntervals(intervalsToTransfer);
					return;
				}

				// divide and conquer
				int halfOfIntervals = (int) Math.floor(intervals / 2);
				List<IntervalBase> firstHalfIntervals = intervalsToTransfer
						.subList(0, halfOfIntervals);
				List<IntervalBase> secondHalfIntervals = intervalsToTransfer
						.subList(halfOfIntervals, intervals);
				transferIntervals(firstHalfIntervals);
				transferIntervals(secondHalfIntervals);
				break;
			}

		}

		private void resetDatabase() {
			if (intervalPersister.getSize() <= 0) {
				intervalPersister.clearAndResetMap();
			}
		}
	}
}
