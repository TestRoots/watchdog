package nl.tudelft.watchdog.intellij.logic.interval;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.logic.ui.RegularCheckerBase;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

/**
 * This manager takes care of the repeated transferal of all closed intervals to
 * the server. When the transfer to the server was successful, the intervals are
 * immediately deleted from the local database. Furthermore, it allows the
 * immediate execution of this regularly scheduled task, e.g. when it is needed
 * on exiting.
 */
public class IntervalTransferManager extends RegularCheckerBase {

	private static final int UPDATE_RATE = 3 * 60 * 1000;

	/**
	 * Constructor. Tries to immediately transfer all remaining intervals, and
	 * sets up a scheduled timer to run every {@value #UPDATE_RATE}
	 * milliseconds.
	 */
	public IntervalTransferManager(final IntervalPersister intervalPersister) {
		super(UPDATE_RATE);
		task = new IntervalsTransferTimerTask(intervalPersister);
		runSetupAndStartTimeChecker();
	}

	/** Immediately synchronizes the intervals with the server. */
	public void sendIntervalsImmediately() {
		task.run();
	}

	private static class IntervalsTransferTimerTask extends TimerTask {
		private final IntervalPersister intervalPersister;
		private final Preferences preferences;

		private IntervalsTransferTimerTask(IntervalPersister intervalPersister) {
			this.intervalPersister = intervalPersister;
			this.preferences = Preferences.getInstance();
		}

		/**
		 * Transfers all intervals from the persistence storage that are not yet
		 * on the server, to the server.
		 */
		@Override
		public void run() {
            if(intervalPersister.isClosed()) return;
			List<IntervalBase> intervalsToTransfer = new ArrayList<IntervalBase>(
					intervalPersister.readIntervals());

			if (intervalsToTransfer.isEmpty()) {
				return;
			}

			transferIntervals(intervalsToTransfer);
			resetDatabase();
		}

		private void transferIntervals(List<IntervalBase> intervalsToTransfer) {
			JsonTransferer intervalTransferer = new JsonTransferer();

			Connection connection = intervalTransferer
					.sendIntervals(intervalsToTransfer, WatchDogUtils.getProjectName());
			switch (connection) {
			case SUCCESSFUL:
				intervalPersister.removeIntervals(intervalsToTransfer);
				preferences.setLastTransferedInterval();
				preferences.addTransferedIntervals(intervalsToTransfer.size());
				WatchDogGlobals.lastTransactionFailed = false;
				break;

			case NETWORK_ERROR:
				if (WatchDogGlobals.lastTransactionFailed == true) {
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
